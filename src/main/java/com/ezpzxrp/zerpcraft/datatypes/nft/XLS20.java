package com.ezpzxrp.zerpcraft.datatypes.nft;

import com.ezpzxrp.zerpcraft.ZerpCraft;
import org.apache.commons.codec.binary.Hex;
import org.json.simple.JSONObject;

public class XLS20 {

    private String tokenId;
    private String hex;
    String serverName;
    String worldName;
    int chunkSize;
    String prefixFlag;
    int x1;
    int z1;
    int x2;
    int z2;

    public XLS20(XLS19 xls19Nft) {

    }

    public XLS20(JSONObject nftObject) {

        this((String) nftObject.get("NFTokenID"), (String) nftObject.get("URI"));

    }

    public XLS20(String tokenId, String URI) {

        this.hex = URI;
        String[] nftSegments = parseNFT();
        System.out.println(nftSegments[0]);
        this.serverName = nftSegments[0];
        this.worldName = nftSegments[1];
        this.chunkSize = Integer.parseInt(nftSegments[2]);
        this.prefixFlag = nftSegments[3];
        this.x1 = Integer.parseInt(nftSegments[4]);
        this.z1 = Integer.parseInt(nftSegments[5]);
        this.x2 = Integer.parseInt(nftSegments[6]);
        this.z2 = Integer.parseInt(nftSegments[7]);
        this.tokenId = tokenId;
    }

    public String getSellOffer(String regionId) {

        String offerIndex = (String) ZerpCraft.p.xls20NFTInfo.get(regionId).get("offerIndex");

        return offerIndex;
    }

    public String getTokenId() {

        return tokenId;
    }

    private String[] parseNFT() {

        String decodedNFT = decodeNFT(hex);
        return decodedNFT.split(":");
    }

    private String decodeNFT(String hex) {

        String decodedNFT = "";
        byte[] bytes = new byte[0];
        try {

            bytes = Hex.decodeHex(hex.toCharArray());
            decodedNFT = new String(bytes, "UTF-8");
        }
        catch (Exception e) {

            return this.hex;
        }
        return decodedNFT;
    }

    public String getWorldGuardRegionName() {

        return prefixFlag + chunkSize + "x" + getX1() + "z" + getZ1() + "X" + getX2() + "Z";
    }

    public String getPrefixFlag() {return prefixFlag;}
    public int getChunkSize() {return chunkSize;}
    public int getX1() {return x1;}
    public int getZ1() {return z1;}
    public int getX2() {return x2;}
    public int getZ2() {return z2;}

}
