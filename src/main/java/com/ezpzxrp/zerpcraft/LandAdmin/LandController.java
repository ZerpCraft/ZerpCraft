package com.ezpzxrp.zerpcraft.LandAdmin;

import com.ezpzxrp.zerpcraft.LandAdmin.runnables.NFTLocationMonitoringTask;
import com.ezpzxrp.zerpcraft.LandAdmin.runnables.NFTResyncTask;
import com.ezpzxrp.zerpcraft.LandAdmin.runnables.XRPLNFTSyncTask;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.datatypes.nft.XLS20;
import com.ezpzxrp.zerpcraft.datatypes.player.PlayerService;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.datatypes.worldguard.Utilities;
import com.ezpzxrp.zerpcraft.util.ResponseParser;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.flags.Flags;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.*;


public class LandController {

    public static void resyncNFTs() {

        new NFTResyncTask().runTaskLaterAsynchronously(ZerpCraft.p, 60);
    }

    public static void serverStartupLandAdministrationRoutine() {

        new XRPLNFTSyncTask().runTask(ZerpCraft.p);
        new NFTLocationMonitoringTask().runTaskLaterAsynchronously(ZerpCraft.p, 60);
    }

    public static void clearOutPlayerRegionOwnership(ZerpCraftPlayer zcPlayer) {

        World world = Bukkit.getServer().getWorld("world_1640738270");
        com.sk89q.worldedit.world.World worldEdit = BukkitAdapter.adapt(world);
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        RegionManager regions = container.get(worldEdit);
        DefaultDomain owners = null;
        for (String regionKey : regions.getRegions().keySet()) {

            System.out.println(regionKey);
            owners = regions.getRegion(regionKey).getOwners();
        }
        System.out.println(zcPlayer.getUuid());
        try {

            owners.removePlayer(zcPlayer.getUuid());
        }
        catch (NullPointerException e) {}
    }

    public static Boolean matchPlayerToPublicAddress(Object nftHash, Map<String, ZerpCraftPlayer> registeredPlayersByPublicAddress) {

            JSONObject hashEntry = (JSONObject) nftHash;
            String publicAddress = (String) hashEntry.get("publicAddress");
            if (registeredPlayersByPublicAddress.get(publicAddress) != null) {

                System.out.println("Player match. Player owns NFT on this server");
                return true;
            }

        return false;
    }

    public static void orchestrateLandTransfer(JSONObject transferEvent) {

        //Setup NFT
        String URI = (String) transferEvent.get("URI");
        System.out.println(URI);
        String NFTId = (String) transferEvent.get("NFTId");
        XLS20 nft = new XLS20(NFTId, URI);

        ProtectedRegion plot = Utilities.getWGRegion(nft);
        String walletAddress = (String) transferEvent.get("DestinationWallet");
        transferRegion(plot, nft, walletAddress);
    }

    public static void transferRegion(ProtectedRegion plot, XLS20 nft, String walletAddress) {

        if (plot == null) {

            createRegion(nft, walletAddress);
        }
        else {

            transferRegion(plot, walletAddress, nft);
        }
    }

    public static ProtectedRegion createRegion(XLS20 nft, String walletAddress) {

        int[] worldYAxis = WorldUtils.getYAxisByServerVersion(ZerpCraft.p.getRunningVersion());

        BlockVector3 min = BlockVector3.at(nft.getX1(), worldYAxis[0], nft.getZ1());
        BlockVector3 max = BlockVector3.at(nft.getX2(), worldYAxis[1], nft.getZ2());
        String regionId =  nft.getPrefixFlag() + nft.getChunkSize() + "x" + nft.getX1() +  "z" + nft.getZ1() + "X" + nft.getX2() + "Z" + nft.getZ2();

        ProtectedRegion plot = new ProtectedCuboidRegion(regionId, min, max);
        ZerpCraftPlayer zcPlayer = PlayerService.getPlayerByWallet(walletAddress);
        if (zcPlayer != null) {

            DefaultDomain members = plot.getMembers();
            members.addPlayer(zcPlayer.getUuid());
            plot.setOwners(members);
        }
        plot.setPriority(10);
        plot.setFlag(Flags.INTERACT, StateFlag.State.ALLOW);
        plot.setFlag(Flags.DENY_MESSAGE, "This land is owned by someone else!");
        plot.setFlag(ZerpCraft.p.IS_ZERPCRAFT_FLAG, true);
        plot.setFlag(ZerpCraft.p.NFT_ID_FLAG, nft.getTokenId());
        plot.setFlag(ZerpCraft.p.WALLET_ADDRESS_FLAG, walletAddress);

        Utilities.addRegion(plot);
        return plot;
    }

    public static void transferRegion(ProtectedRegion plot, String walletAddress, XLS20 nft) {

        //TODO: Does this remove the owner as well?
        DefaultDomain members = plot.getMembers();
        members.removeAll();
        DefaultDomain owners = plot.getOwners();
        owners.removeAll();

        ZerpCraftPlayer zcPlayer = PlayerService.getPlayerByWallet(walletAddress);
        members.addPlayer(zcPlayer.getUuid());
        owners.addPlayer(zcPlayer.getUuid());
        plot.setFlag(Flags.INTERACT, StateFlag.State.ALLOW);
        plot.setFlag(Flags.DENY_MESSAGE, "This land is owned by someone else!");
        plot.setFlag(ZerpCraft.p.WALLET_ADDRESS_FLAG, walletAddress);
        plot.setFlag(ZerpCraft.p.NFT_ID_FLAG, nft.getTokenId());
        plot.setFlag(ZerpCraft.p.IS_ZERPCRAFT_FLAG, true);
    }

    public static JSONObject parseNFTokenOfferAcceptTxToTransferEvent(JSONObject tx) {

        JSONObject transferEvent = new JSONObject();
        JSONObject deletedNode = pullOutDeletedNodeFromTx(tx);
        System.out.println(deletedNode);
        String NFTokenId = ResponseParser.getValueFromJsonKeypath(deletedNode, "DeletedNode.FinalFields.NFTokenID");

        transferEvent.put("NFTId", NFTokenId);
        transferEvent.put("URI", pullURIFromTx(tx, NFTokenId));
        transferEvent.put("DestinationWallet", ResponseParser.getValueFromJsonKeypath(deletedNode, "DeletedNode.FinalFields.Destination"));
        transferEvent.put("SourceWallet", ResponseParser.getValueFromJsonKeypath(deletedNode, "DeletedNode.FinalFields.Owner"));
        transferEvent.put("EventNumber", 1);
        transferEvent.put("TxHash",ResponseParser.getValueFromJsonKeypath(tx, "transaction.hash"));
        transferEvent.put("EventType", "Transfer");

        return transferEvent;
    }

    public static JSONObject pullOutDeletedNodeFromTx(JSONObject tx) {

        JSONArray affectedNodes = ResponseParser.getArrayFromJsonKeypath(tx,"meta.AffectedNodes");
        for (Object node : affectedNodes) {

            JSONObject jNode = (JSONObject) node;
            if (jNode.get("DeletedNode") != null &&
                    ResponseParser.getValueFromJsonKeypath(jNode,"DeletedNode.LedgerEntryType").equals("NFTokenOffer")) {

                return jNode;
            }
        }
        return null;
    }

    public static String pullURIFromTx(JSONObject tx, String NFTokenId ) {

        JSONArray affectedNodes = ResponseParser.getArrayFromJsonKeypath(tx, "meta.AffectedNodes");

        for (Object node : affectedNodes) {

            JSONObject jNode = (JSONObject) node;
            if (jNode.get("CreatedNode") != null && ResponseParser.getValueFromJsonKeypath(jNode, "CreatedNode.LedgerEntryType").equals("NFTokenPage")) {

                JSONArray newNFTObject = ResponseParser.getArrayFromJsonKeypath(jNode, "CreatedNode.NewFields.NFTokens");
                JSONObject newNFT = (JSONObject) newNFTObject.get(0);
                String id = ResponseParser.getValueFromJsonKeypath(newNFT, "NFToken.NFTokenID");
                if (id.equals(NFTokenId)) {

                    return  ResponseParser.getValueFromJsonKeypath(newNFT, "NFToken.URI");
                }
            }
            if (jNode.get("ModifiedNode") != null && ResponseParser.getValueFromJsonKeypath(jNode, "ModifiedNode.LedgerEntryType").equals("NFTokenPage")) {

                for (Object finalNode : ResponseParser.getArrayFromJsonKeypath(jNode, "ModifiedNode.FinalFields.NFTokens")) {

                    JSONObject jFinalNode = (JSONObject) finalNode;
                    JSONObject NFToken = (JSONObject) jFinalNode.get("NFToken");
                    if (NFToken.get("NFTokenID").equals(NFTokenId)) {

                        return (String) NFToken.get("URI");
                    }
                }
            }
        }
        return null;
    }

    public static JSONObject parseNFTResponse(JSONObject response) {

        JSONObject nftObject = new JSONObject();
        ResponseParser.getValueFromJsonKeypath(response, "result.nft_id");

        nftObject.put("NFTId", ResponseParser.getValueFromJsonKeypath(response, "result.nft_id"));
        nftObject.put("URI", ResponseParser.getValueFromJsonKeypath(response, "result.uri"));
        nftObject.put("DestinationWallet", ResponseParser.getValueFromJsonKeypath(response, "result.owner"));
        nftObject.put("SourceWallet", null);
        nftObject.put("EventNumber", 1);
        nftObject.put("TxHash",null);
        nftObject.put("EventType", "NFTInfo");

        return nftObject;
    }
}

