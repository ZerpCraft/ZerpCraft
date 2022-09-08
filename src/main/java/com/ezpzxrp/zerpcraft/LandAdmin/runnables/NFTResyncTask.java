package com.ezpzxrp.zerpcraft.LandAdmin.runnables;

import com.ezpzxrp.zerpcraft.XRPL.DataHelper;
import com.ezpzxrp.zerpcraft.ZerpCraft;
import com.ezpzxrp.zerpcraft.callouts.XRPLCluster;
import com.ezpzxrp.zerpcraft.datatypes.nft.Plot;
import com.ezpzxrp.zerpcraft.datatypes.nft.XLS19;
import com.ezpzxrp.zerpcraft.datatypes.player.ZerpCraftPlayer;
import com.ezpzxrp.zerpcraft.util.ResponseParser;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.simple.JSONObject;

import java.util.Map;
import java.util.UUID;

public class NFTResyncTask extends BukkitRunnable {

    public NFTResyncTask() {}

    public void run() {

        Map<UUID, ZerpCraftPlayer> registeredPlayers = ZerpCraft.p.registeredPlayers;

        XRPLCluster publicNode = new XRPLCluster();
        for (UUID uuid : registeredPlayers.keySet()) {

            boolean tryRequestAgain = true;
            ZerpCraftPlayer zcPlayer = ZerpCraft.p.registeredPlayers.get(uuid);

            if (zcPlayer.getHasNFT() && zcPlayer.getHasNewNFT()) {

                JSONObject responseObject = publicNode.getWalletAccountInfo(zcPlayer.getProfile().getXrplAddress());
                String xls19NftDomain = ResponseParser.decodeDomain(responseObject);
                int nftCount = DataHelper.countNFTsInDomain(xls19NftDomain);
                if (nftCount < zcPlayer.getNFTCount()) {
                    continue;
                }
                zcPlayer.setNFTCount(nftCount);
                if (!xls19NftDomain.equals("")) {

                    try {

                        String theString = xls19NftDomain.substring(xls19NftDomain.indexOf('['), xls19NftDomain.indexOf(']') + 1);
                        String[] splittedString = theString.substring(1, theString.length()-1).split(",");
                        System.out.println("Focusing on " + zcPlayer.getPlayerName());

                        for (String hash : splittedString) {

                            try {

                                XLS19 nft = new XLS19(hash);
                                zcPlayer.unlockPlots();

                                if (zcPlayer.getPlot(hash) != null) {

                                    zcPlayer.getPlot(hash).lock();
                                }
                                else {

                                    tryRequestAgain = false;
                                    System.out.println("Creating a plot");
                                    Plot plot = new Plot(zcPlayer.getProfile().getXrplAddress(),nft, zcPlayer.getUuid());
                                    zcPlayer.addPlot(plot.getHash(), plot);
                                    if (zcPlayer.getPlayer() != null) {
                                        if(zcPlayer.getPlayer().isOnline()) {
                                            zcPlayer.getPlayer().sendMessage("You've got a new plot assigned to your player! Use '/rg list -p " + zcPlayer.getPlayer().getName() + "' to see your plots");
                                        }
                                    }

                                }
                            }
                            catch (Exception e) {

                                System.out.println("Could not create NFT. Bad hash?");
                                tryRequestAgain = true;
                                e.printStackTrace();
                            }
                        }
                        ZerpCraft.p.registeredPlayers.put(zcPlayer.getUuid(), zcPlayer);
                    }
                    catch (Exception e) {

                        tryRequestAgain = true;
                        System.out.println("Could not create NFT. Bad hash?");
                    }
                }
                else {
                    zcPlayer.unlockPlots();
                }
                if (tryRequestAgain) {

                    zcPlayer.setHasNewNFT(true);
                }
                else {

                    zcPlayer.setHasNewNFT(false);
                }
            }
/*            try {
                //Thread.sleep(150);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }*/
        }
    }

}
