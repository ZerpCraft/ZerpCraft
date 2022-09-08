package com.ezpzxrp.zerpcraft;

import com.ezpzxrp.zerpcraft.LandAdmin.LandController;
import com.ezpzxrp.zerpcraft.XUMM.XUMM;
import com.ezpzxrp.zerpcraft.commands.CommandController;
import com.ezpzxrp.zerpcraft.config.Config;
import com.ezpzxrp.zerpcraft.database.DatabaseManagerFactory;
import com.ezpzxrp.zerpcraft.database.IDatabaseManager;
import com.ezpzxrp.zerpcraft.datatypes.player.PlayerProfile;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.listener.PaymentListener;
import com.ezpzxrp.zerpcraft.listener.PlayerListener;
import com.ezpzxrp.zerpcraft.runnables.player.PlayerProfileLoadingTask;
import com.ezpzxrp.zerpcraft.util.LogFilter;
import com.ezpzxrp.zerpcraft.util.player.UserManager;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.flags.BooleanFlag;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StringFlag;
import com.sk89q.worldguard.protection.flags.registry.FlagConflictException;
import com.sk89q.worldguard.protection.flags.registry.FlagRegistry;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.io.File;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;


public final class ZerpCraft extends JavaPlugin {

    public static ZerpCraft p;
    public ZerpCraft() {p = this;}

    /* Constants */
    private static boolean serverShutdownExecuted = false;
    private static String runningVersion;

    /* Managers */
    private static IDatabaseManager databaseManager;

    /* File Paths */
    private static String mainDirectory;
    private static String flatFileDirectory;
    private static String playerXUMMRegistrationFile;
    private static String auctionBidHistoryFile;
    private static String foundersNFTStateMappingFile;
    private static String purchaseHistoryFile;
    private static String purchaseCohortsFile;
    private static String xls20MigrationFile;

    /* Update Checker */
    // Need to update this once I post ZerpCraft
    private static final int SPIGOT_RESOURCE_ID = 90800;

    /* Metadata Values */
    public final static String playerDataKey = "ZerpCraft: Player Data";
    public  Map<UUID, ZerpCraftPlayer> registeredPlayers = new HashMap<UUID, ZerpCraftPlayer>();
    public Map<Integer, Boolean[]> fNFTMap = new HashMap<Integer, Boolean[]>();
    public int openLandIncrement = 1;
    public boolean openLandSale = true;
    public Map<String, LocalDateTime> lockedLand = new HashMap<String, LocalDateTime>();
    public int FNFTPrice = 184;
    public Map<String, JSONObject> xls20NFTInfo = new HashMap<String, JSONObject>();
    public Map<String,JSONObject> roles = new HashMap<String,JSONObject>();

    /* Providers */
    private static XUMM xumm;
    public LuckPerms api;

    /* Auction Values */
    public int currentHighestBid = 0;
    public int currentBidInterval = 1;
    public String currentHighestBidder;
    public String auctionWinner;
    public boolean isAuctionOpen = true;

    public StringFlag WALLET_ADDRESS_FLAG;
    public StringFlag NFT_ID_FLAG;
    public BooleanFlag IS_ZERPCRAFT_FLAG;

    public CountDownLatch websocketLatch;

    @Override
    public void onLoad() {

        FlagRegistry registry = WorldGuard.getInstance().getFlagRegistry();
        try {

            StringFlag walletAddressFlag = new StringFlag("wallet-address", "none");
            StringFlag NFTIdFlag = new StringFlag("NFT-Id", "none");
            BooleanFlag isZerpCraftFlag = new BooleanFlag("is-zerpcraft");
            registry.register(walletAddressFlag);
            registry.register(NFTIdFlag);
            registry.register(isZerpCraftFlag);
            WALLET_ADDRESS_FLAG = walletAddressFlag;
            NFT_ID_FLAG = NFTIdFlag;
            IS_ZERPCRAFT_FLAG = isZerpCraftFlag;
        }
        catch (FlagConflictException ignored) {}
    }

    @Override
    public void onEnable() {
        // Plugin startup logic

        getLogger().setFilter(new LogFilter(this));

/*        getLogger().info("Update checker running");
        UpdateChecker.init(this, SPIGOT_RESOURCE_ID).checkEveryXHours(24).checkNow();*/

        getLogger().info("Getting Mc version");
        runningVersion = Bukkit.getVersion().substring(Bukkit.getVersion().indexOf(':') + 2,Bukkit.getVersion().length() - 1);

        getLogger().info("Setup file paths");
        setupFilePaths();

        getLogger().info("Initializing database");
        databaseManager = DatabaseManagerFactory.getDatabaseManager();

        getLogger().info("Starting command registration");
        CommandController.registerCommands();

        getLogger().info("Starting event registration");
        registerEvents();

        getLogger().info("Setup LuckPerms provider");
        RegisteredServiceProvider<LuckPerms> provider = Bukkit.getServicesManager().getRegistration(LuckPerms.class);
        this.api = provider.getProvider();

        getLogger().info("Saving default config");
        p.saveDefaultConfig();

        getLogger().info("Initialize Config");
        initializeConfig();

        getLogger().info("Setup XUMM");
        try {
            setupXUMM();
        } catch (XUMM e) {
            e.printStackTrace();
        }

        getLogger().info("Loading registered players");
        loadRegisteredPlayers();
        getLogger().info("Loading purchase cohorts");
        getLogger().info("System time is" + new Date(System.currentTimeMillis()));
        loadPurchaseCohorts();
        //loadFNFTStateMap();
        loadAuctionState();
        loadLockedLand();
        loadXLS20Migration();

        //getLogger().info("Reading in purchase history. Setting current land increment");
        //setLandAutoNumber();

        getLogger().info("Scheduling Land Administrator");
        scheduleLandAdmin();


        getLogger().info("Startup XLS20 Land Administration.");
        LandController.serverStartupLandAdministrationRoutine();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

        websocketLatch.countDown();
        setServerShutdown(true);

        try {

            UserManager.saveAll();
            UserManager.clearAll();
        }
        catch (Exception e) {

            e.printStackTrace();
        }
    }

    public static String getPlayerXUMMRegistrationFilePath() {

        return playerXUMMRegistrationFile;
    }


    private void setupFilePaths() {

        mainDirectory = getDataFolder().getPath() + File.separator;
        getLogger().info("mainDirectory: " + mainDirectory);
        flatFileDirectory = mainDirectory + "flatfile" + File.separator;
        getLogger().info("flatFileDirectory: " + flatFileDirectory);
        playerXUMMRegistrationFile = flatFileDirectory + "ZerpCraft.registration";
        auctionBidHistoryFile = flatFileDirectory + "AuctionHistory.txt";
        foundersNFTStateMappingFile = flatFileDirectory + "FNFTState.txt";
        purchaseHistoryFile = flatFileDirectory + "PurchaseHistory.txt";
        purchaseCohortsFile = flatFileDirectory + "PurchaseCohorts.txt";
        xls20MigrationFile = flatFileDirectory + "Matching.json";
    }

    private void initializeConfig() {

        List<Map<?,?>> configRoles = Config.getInstance().getRoles();
        for (Map<?,?> role : configRoles) {

            String prefix = (String) role.get("Prefix");
            JSONObject roleObject = new JSONObject();
            roleObject.put("RoleName", role.get("RoleName"));
            System.out.println(roleObject);
            this.roles.put(prefix, roleObject);
        }
    }

    private void registerEvents() {

        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new PlayerListener(), this);
        pluginManager.registerEvents(new PaymentListener(), this);
    }

    private void scheduleLandAdmin() {

        new BukkitRunnable() {
            @Override
            public void run() {

                getLogger().info("Land Admin is running");
                LandController.resyncNFTs();
            }
        }.runTaskTimer(p,0L, 600L);
    }

    private void loadXLS20Migration() {

        xls20NFTInfo = databaseManager.loadXLS20Objects();
    }

    private void loadRegisteredPlayers() {

        Map<UUID, PlayerProfile> registeredPlayersProfiles = databaseManager.loadRegisteredPlayers();
        net.luckperms.api.model.user.UserManager userManager = api.getUserManager();
        for (UUID playerUUID : registeredPlayersProfiles.keySet()) {

            if (this.registeredPlayers.get(playerUUID) == null) {

                ZerpCraftPlayer zcPlayer = new ZerpCraftPlayer(registeredPlayersProfiles.get(playerUUID));
                this.registeredPlayers.put(playerUUID, zcPlayer);
                CompletableFuture<User> userFuture = userManager.loadUser(playerUUID);

                userFuture.thenAcceptAsync((user -> {
                    InheritanceNode node = InheritanceNode.builder("registered").value(true).build();
                    DataMutateResult result = user.data().add(node);
                    userManager.saveUser(user);
                }));



            }
        }
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {

            new PlayerProfileLoadingTask(player).runTaskLaterAsynchronously(ZerpCraft.p, 60);
        }
    }

    private void loadFNFTStateMap() {

        databaseManager.loadFNFTStateMap();
    }


    private void loadAuctionState() {

        System.out.println("Loading current auction winner");
        String[] auctionState = databaseManager.loadAuctionState();
        this.currentHighestBid = Integer.parseInt(auctionState[1]);
        this.currentHighestBidder = auctionState[0];
    }

    private void setLandAutoNumber() {

        Map<Integer, String[]> purchaseHistory = databaseManager.loadPurchaseHistory();
        int increment = 0;
        for(int i : purchaseHistory.keySet()) {

            String[] purchaseLine = purchaseHistory.get(i);
            ZerpCraftPlayer zcplayer = registeredPlayers.get(UUID.fromString(purchaseLine[0]));
            zcplayer.setHasNFT(true);
/*            if (purchaseLine[1].equals("OpenLand")) {

                zcplayer.getProfile().setPurchasePermission(true);
            }*/
            if (purchaseLine[1].equals("PXX")) {
                continue;
            }
            if (purchaseLine[1].equals("FXX")) {

                zcplayer.setIsFNFTOwner(true);
                registeredPlayers.put(UUID.fromString(purchaseLine[0]), zcplayer);
                continue;
            }
            if (Integer.parseInt(purchaseLine[2]) > increment) {

                increment = Integer.parseInt(purchaseLine[2]);
            }
        }
        this.openLandIncrement = increment;
        getLogger().info("Open Land increment set to " + this.openLandIncrement);
    }

    private void loadPurchaseCohorts() {

        System.out.println("I'm inside the purchase cohort");
        Map<Integer, String> purchaseCohorts = databaseManager.loadPurchaseCohorts();
        try {

            DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date purchaseDate = fmt.parse("2022-03-27 13:00:00");
            int cohortCounter = 1;
            int cohortNumber = 1;

            for(int i : purchaseCohorts.keySet()) {

                try {

                    OfflinePlayer player = Bukkit.getOfflinePlayer(purchaseCohorts.get(i));
                    UUID uuid = player.getUniqueId();

                    ZerpCraftPlayer zcPlayer = registeredPlayers.get(uuid);
                    zcPlayer.getProfile().setPurchaseDate(purchaseDate);
                    //System.out.println(zcPlayer.getPlayerName() + " was loaded in purchase cohort " + cohortNumber + " with a purchase time of " + purchaseDate);

                    if (cohortCounter % 15 == 0) {

                        Calendar cl = Calendar.getInstance();
                        cl.setTime(purchaseDate);
                        cl.add(Calendar.HOUR, 1);
                        purchaseDate = cl.getTime();
                        cohortNumber++;
                    }
                    cohortCounter++;
                }
                catch (Exception e) {

                    for (UUID uuid : registeredPlayers.keySet()) {

                        ZerpCraftPlayer zcPlayer = registeredPlayers.get(uuid);
                        if(zcPlayer.getPlayerName().toLowerCase(Locale.ROOT).equals(purchaseCohorts.get(i).toLowerCase(Locale.ROOT))) {

                            zcPlayer.getProfile().setPurchaseDate(purchaseDate);
                            //System.out.println(zcPlayer.getPlayerName() + " was loaded in purchase cohort " + cohortNumber + " with a purchase time of " + purchaseDate);

                            if (cohortCounter % 15 == 0) {

                                Calendar cl = Calendar.getInstance();
                                cl.setTime(purchaseDate);
                                cl.add(Calendar.HOUR, 1);
                                purchaseDate = cl.getTime();
                                cohortNumber++;
                            }
                            cohortCounter++;
                        }
                    }
                }
            }
        }
        catch (ParseException e) { e.printStackTrace(); }

    }

    private void loadLockedLand() {

        System.out.println("Starting to load locked land");
        World world = Bukkit.getServer().getWorld("world_1640738270");
        com.sk89q.worldedit.world.World worldEdit = BukkitAdapter.adapt(world);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(worldEdit);

        Map<String,ProtectedRegion> regionMap =  regions.getRegions();
        for (String regionKey :  regionMap.keySet()) {

            if (!regionKey.equals("__global__") && !regionKey.substring(0,3).equals("fxx")) {
                ProtectedRegion region = regions.getRegion(regionKey);
                String landLockKey = "Regions:" + region.getMinimumPoint().getBlockX() + ":" + region.getMinimumPoint().getBlockZ() + ":" +
                        region.getMaximumPoint().getBlockX() + ":" + region.getMaximumPoint().getBlockZ();
                lockedLand.put(landLockKey, LocalDateTime.now().minusDays(1));
                region.setFlag(Flags.DENY_MESSAGE, "This land is owned by someone else!");
                regions.addRegion(region);
            }
        }
    }

    private void setServerShutdown(boolean state) {

        serverShutdownExecuted = state;
    }

    public static String getRunningVersion() { return runningVersion; }
    public static XUMM getXumm() { return xumm; }
    public static IDatabaseManager getDatabaseManager() { return databaseManager; }
    public static String getAuctionBidHistoryFilePath() { return auctionBidHistoryFile; }
    public static String getFoundersNFTStateMapFilePath() { return foundersNFTStateMappingFile; }
    public static String getPlayerPaymentPreferencesFilePath() { return playerXUMMRegistrationFile; }
    public static String getPurchaseHistoryFilePath() { return purchaseHistoryFile; }
    public static String getPurchaseCohortsFilePath() { return purchaseCohortsFile; }
    public static String getXLS20MigrationFilePath() { return xls20MigrationFile; }
    public static void setupXUMM() throws XUMM { xumm = new XUMM(); }
    public static synchronized boolean isServerShutdownExecuted() {
        return serverShutdownExecuted;
    }
}
