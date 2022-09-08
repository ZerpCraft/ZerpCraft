package com.ezpzxrp.zerpcraft.commands;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import org.bukkit.command.PluginCommand;

public class CommandController {

    public static void registerCommands() {

        registerXUMMRegistrationCommand();
        registerSendCommand();
        registerGetPlayerXUMMTokenCommand();
        registerAuctionBid();
        registerSetAuctionBidThreshold();
        registerSetAuctionBidIncrement();
        registerGetBidThreshold();
        registerCloseAuctionCountdown();
        registerGetAuctionWinnerName();
        registerRequestPayment();
        registerStampNFT();
        registerToggleFNFT();
        registerPos1();
        registerPos2();
        registerToggleLandSale();
        registerToggleFNFTPrice();
        registerProfile();
        registerTogglePurchase();
        registerTogglePurchasing();
        registerProvisionNFTs();
        registerDomainOverride();
        registerXls20();
        registerOptIn();
        registerOptOut();
        registerNFTSync();
        registerDeleteRegistration();
    }

    public static void registerXUMMRegistrationCommand() {

        PluginCommand command = ZerpCraft.p.getCommand("zcRegister");
        command.setDescription("Register your XUMM wallet with this server");
        command.setExecutor(new RegisterXUMMWallet());
    }

    public static void registerSendCommand() {

        PluginCommand command = ZerpCraft.p.getCommand("zcSend");
        command.setDescription("Send XRP to another player! /zcsend {playerName} {amount}");
        command.setExecutor(new SendPlayerXRP());
    }

    public static void registerGetPlayerXUMMTokenCommand() {

        PluginCommand command = ZerpCraft.p.getCommand("zcGetToken");
        command.setDescription("An admin command that returns the player's XUMM token. /zcGetToken {playerName}");
        command.setExecutor(new getPlayerXUMMToken());
    }

    public static void registerAuctionBid() {

        PluginCommand command = ZerpCraft.p.getCommand("zcBid");
        command.setDescription("Increases the bid floor either by the input of the minimum increment /zcBid {bidNumber} or {minimum} ");
        command.setExecutor(new SetAuctionBid());
    }

    public static void registerSetAuctionBidThreshold() {

        PluginCommand command = ZerpCraft.p.getCommand("zcSetBidThreshold");
        command.setDescription("An admin command that sets the bidding threshold to a new number");
        command.setExecutor(new SetAuctionBidThreshold());
    }

    public static void registerSetAuctionBidIncrement() {

        PluginCommand command = ZerpCraft.p.getCommand("zcSetBidIncrement");
        command.setDescription("An admin command that sets the bidding increment to a new number");
        command.setExecutor(new SetAuctionBidIncrement());
    }

    public static void registerGetBidThreshold() {

        PluginCommand command = ZerpCraft.p.getCommand("zcGetBidThreshold");
        command.setDescription("An admin command that returns/broadcasts the current highest bid + player /zcGetBidThreshold {0|1}");
        command.setExecutor(new GetAuctionBidThreshold());
    }

    public static void registerCloseAuctionCountdown() {

        PluginCommand command = ZerpCraft.p.getCommand("zcCloseAuctionCountdown");
        command.setDescription("An admin command that starts the countdown to close an auction");
        command.setExecutor(new StartCloseAuctionCountdown());
    }

    public static void registerGetAuctionWinnerName() {

        PluginCommand command = ZerpCraft.p.getCommand("zcGetAuctionWinnerName");
        command.setDescription("An admin command that returns the latest auction winner's name");
        command.setExecutor(new GetAuctionWinnerName());
    }

    public static void registerRequestPayment() {

        PluginCommand command = ZerpCraft.p.getCommand("zcRequestPayment");
        command.setDescription("An admin command that requests an xrp payment from a player /zcRequestPayment {playerName} {amount}");
        command.setExecutor(new RequestPayment());
    }

    public static void registerStampNFT() {

        PluginCommand command = ZerpCraft.p.getCommand("zcStampNFT");
        command.setDescription("An admin command that take a player name and assigns a new NFT to their wallet");
        command.setExecutor(new StampNFT());
    }

    public static void registerToggleFNFT() {

        PluginCommand command = ZerpCraft.p.getCommand("zcToggleFNFT");
        command.setDescription("An admin command lock/unlocks a range of FNFTs");
        command.setExecutor(new ToggleFNFT());
    }

    public static void registerPos1() {

        PluginCommand command = ZerpCraft.p.getCommand("zcPos1");
        command.setDescription("Sets the first point to select land for purchase");
        command.setExecutor(new Pos1());
    }

    public static void registerPos2() {

        PluginCommand command = ZerpCraft.p.getCommand("zcPos2");
        command.setDescription("Sets the first point to select land for purchase");
        command.setExecutor(new Pos2());
    }

    public static void registerToggleLandSale() {

        PluginCommand command = ZerpCraft.p.getCommand("zcToggleLandSale");
        command.setDescription("Sets the first point to select land for purchase");
        command.setExecutor(new ToggleLandSale());
    }

    public static void registerToggleFNFTPrice() {

        PluginCommand command = ZerpCraft.p.getCommand("zcToggleFNFTPrice");
        command.setDescription("Toggle price");
        command.setExecutor(new ToggleFNFTPrice());
    }

    public static void registerProfile() {

        PluginCommand command = ZerpCraft.p.getCommand("zcProfile");
        command.setDescription("Return the player's profile info");
        command.setExecutor(new GetPlayerProfile());
    }

    public static void registerTogglePurchase() {

        PluginCommand command = ZerpCraft.p.getCommand("zcTogglePurchase");
        command.setDescription("Toggle a player's purchasing permission");
        command.setExecutor(new SetPurchasePermission());
    }

    public static void registerTogglePurchasing() {

        PluginCommand command = ZerpCraft.p.getCommand("zcTogglePurchasing");
        command.setDescription("Override the systems purchasing permissions");
        command.setExecutor(new SetPurchasingPermission());
    }

    public static void registerProvisionNFTs() {

        PluginCommand command = ZerpCraft.p.getCommand("zcProvisionNFTs");
        command.setDescription("Forces a players domain to be read the next Land Admin cycle");
        command.setExecutor(new ProvisionNFTs());
    }

    public static void registerDomainOverride() {

        PluginCommand command = ZerpCraft.p.getCommand("zcOverrideDomain");
        command.setDescription("Reconstructs a player's domain from scratch");
        command.setExecutor(new OverrideDomain());
    }

    public static void registerXls20() {

        PluginCommand command = ZerpCraft.p.getCommand("zcXLS20");
        command.setDescription("Migrates player's XLS-19 NFTs to XLS-20");
        command.setExecutor(new XLS20Migration());
    }

    public static void registerOptIn() {

        PluginCommand command = ZerpCraft.p.getCommand("zcOptIn");
        command.setDescription("Makes wallet public to other players");
        command.setExecutor(new OptIn());
    }

    public static void registerOptOut() {

        PluginCommand command = ZerpCraft.p.getCommand("zcOptOut");
        command.setDescription("Makes wallet private to only the server");
        command.setExecutor(new OptOut());
    }

    public static void registerNFTSync() {

        PluginCommand command = ZerpCraft.p.getCommand("zcSync");
        command.setDescription("Checks wallet contents and assigns land");
        command.setExecutor(new NFTSync());
    }

    public static void registerDeleteRegistration() {

        PluginCommand command = ZerpCraft.p.getCommand("zcDeleteRegistration");
        command.setDescription("Removes player wallet registration and all associated wallet ownership");
        command.setExecutor(new DeleteRegistration());
    }

}
