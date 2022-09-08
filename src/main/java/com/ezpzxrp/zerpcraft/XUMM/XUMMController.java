package com.ezpzxrp.zerpcraft.XUMM;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.config.Config;
import com.ezpzxrp.zerpcraft.datatypes.player.PlayerProfile;
import com.ezpzxrp.zerpcraft.datatypes.player.PlayerService;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.runnables.player.PlayerGamemodeCreativeTask;
import com.ezpzxrp.zerpcraft.util.ResponseParser;
import com.ezpzxrp.zerpcraft.util.player.UserManager;
import net.luckperms.api.model.data.DataMutateResult;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public class XUMMController extends Throwable {

    public static void handleRegistration(Player player) throws XUMMController, XUMM {

        ZerpCraftPlayer zcPlayerCheck = ZerpCraft.p.registeredPlayers.get(player.getUniqueId());
        System.out.println(zcPlayerCheck);
        if (zcPlayerCheck != null) {

            if(zcPlayerCheck.getProfile().getXummToken() != null) {

                player.sendMessage( ChatColor.RED + "" + ChatColor.BOLD + "You have already registered. Your currently registered XUMM address is: ");
                player.sendMessage( ChatColor.GREEN + "" + ChatColor.BOLD + zcPlayerCheck.getProfile().getXrplAddress());
                player.sendMessage( ChatColor.RED + "" + ChatColor.BOLD + " If you would like to change your ZerpCraft wallet, click on the link below");
            }
        }
        ZerpCraft.p.getLogger().info("About to get XUMM");
        XUMM xummController = new XUMM();

        ZerpCraft.p.getLogger().info("Performing sign request");
        String signResponse = xummController.signRequest();

        ZerpCraft.p.getLogger().info("Transforming into JSON");
        JSONObject signResponseJson = ResponseParser.stringToJSON(signResponse);

        ZerpCraft.p.getLogger().info("Parsing Keypath");
        String qrLink = ResponseParser.getValueFromJsonKeypath(signResponseJson, "refs.qr_png");

        if (zcPlayerCheck != null) {

            if (zcPlayerCheck.getProfile().getXummToken() != null) {

                player.sendMessage("Click the link below. Scan the provided QR code with XUMM to register");
            }
        }
        player.sendMessage("Link: " + qrLink);

        String registrationUUID = ResponseParser.getValueFromJsonKeypath(signResponseJson, "uuid");
        ZerpCraft.p.getLogger().info("UUID is: " + registrationUUID);

/*        XUMMWebSocketClient websocket = new XUMMWebSocketClient(registrationUUID);
//        Boolean isTimeout = websocket.watchForASign(registrationUUID);
        websocket.addMessageHandler(new XUMMWebSocketClient.MessageHandler() {
            public void handleMessage(String message) {
                System.out.println(message);
                System.out.println(player.getName());
            }
        });*/
        int count = 0;
        String userTokenResponse = null;
        String publicAddress = null;
        JSONObject tokenResponseJson = null;
        ZerpCraft.p.getLogger().info("Entering registration wait loop");
        while (count <= 180) {


            userTokenResponse = xummController.getUserTokenRequest(registrationUUID);
            tokenResponseJson = ResponseParser.stringToJSON(userTokenResponse);
            Boolean signed = ResponseParser.getBooleanFromJsonKeypath(tokenResponseJson,"meta.signed");
            Boolean cancelled = ResponseParser.getBooleanFromJsonKeypath(tokenResponseJson,"meta.cancelled");
            publicAddress = ResponseParser.getValueFromJsonKeypath(tokenResponseJson,"response.account");
            count += 5;
            if(!publicAddress.equals("") && signed && !cancelled) {

                ZerpCraftPlayer zcPlayerDupe = PlayerService.getPlayerByWallet(publicAddress);
                if (zcPlayerDupe == null || !zcPlayerDupe.getProfile().getUniqueId().equals(Objects.requireNonNull(zcPlayerCheck).getProfile().getUniqueId())) {

                    break;
                }
                else {

                    throw new XUMMController("Duplicate registration");
                }
            }
            try{

                Thread.sleep(5000);
            }
            catch(InterruptedException e) {}
        }
        if(count >= 180) {

            throw new XUMMController("Expired registration link");
        }

        ZerpCraft.p.getLogger().info("Setting PlayerProfile public address");
        ZerpCraftPlayer zcPlayer = UserManager.getPlayer(player);
        System.out.println(zcPlayer);
        PlayerProfile playerProfile = zcPlayer.getProfile();
        System.out.println(playerProfile);
        playerProfile.setXrplAddress(publicAddress);

        ZerpCraft.p.getLogger().info("Setting XUMM token to PlayerProfile");
        String xummToken = ResponseParser.getValueFromJsonKeypath(tokenResponseJson, "application.issued_user_token");
        playerProfile.setXummToken(xummToken);
        playerProfile.setChanged();
        ZerpCraft.p.registeredPlayers.put(zcPlayer.getUuid(), zcPlayer);

        net.luckperms.api.model.user.UserManager userManager = ZerpCraft.p.api.getUserManager();
        CompletableFuture<User> userFuture = userManager.loadUser(player.getUniqueId());

        userFuture.thenAcceptAsync((user -> {
            InheritanceNode node = InheritanceNode.builder("registered").value(true).build();
            DataMutateResult result = user.data().add(node);
            userManager.saveUser(user);
        }));

        new PlayerGamemodeCreativeTask(player).runTask(ZerpCraft.p);
        player.sendMessage("Congrats, you've ranked up! Now you can fly around and enjoy Creative mode");
    }

    public static void sendHandler(PlayerProfile senderProfile, double paymentAmount, Player sendingPlayer, ZerpCraftPlayer zcReceivingPlayer) throws XUMMController {

        XUMM xummController = ZerpCraft.getXumm();
        Boolean isValidAmount = xummController.validatePaymentAmount("XRP", paymentAmount);
        if (isValidAmount) {

            ZerpCraft.p.getLogger().info("Making payment request");
            String paymentRequestResponse = xummController.paymentRequest(zcReceivingPlayer.getProfile().getXrplAddress(),
                    paymentAmount, senderProfile.getXummToken(), senderProfile.getXrplAddress());
        }
        else {
            throw new XUMMController("Invalid amount for XRP");
        }
    }

    // This request handler is currently hard coded to work for /zcRequestPayment there is a card to abstract out requests later down the road
    public static void requestHandler(ZerpCraftPlayer zcReceivingPlayer, double paymentAmount ) throws XUMMController {

        XUMM xummController = ZerpCraft.getXumm();
        Boolean isValidAmount = xummController.validatePaymentAmount("XRP", paymentAmount);
        if (isValidAmount) {

            ZerpCraft.p.getLogger().info("Making payment request");
            System.out.println(zcReceivingPlayer.getProfile().getXummToken());
            String paymentRequestResponse = xummController.paymentRequest(Config.getInstance().getWalletAddress(),
                    paymentAmount, zcReceivingPlayer.getProfile().getXummToken(), zcReceivingPlayer.getProfile().getXrplAddress());
            JSONObject paymentRequestJson = ResponseParser.stringToJSON(paymentRequestResponse);
            System.out.println(paymentRequestJson);
            boolean isPushed = ResponseParser.getBooleanFromJsonKeypath(paymentRequestJson, "pushed");
            ZerpCraft.p.getLogger().info("isPushed = " + isPushed);
            if (!isPushed) {

                throw new XUMMController("Push failed");
            }
        }
        else {
            throw new XUMMController("Invalid amount for XRP");
        }
    }

    public XUMMController(String errorMessage) {

        super(errorMessage);
    }


}
