package com.ezpzxrp.zerpcraft.datatypes.nft;

import java.util.Locale;

public class XLS19 {

    private final String hash;
    private String prefixFlag;
    private String issuanceNumber;
    private String serverName;
    private String coordinates;
    private final Integer xCoordinate;
    private final Integer zCoordinate;
    private final Integer XCoordinate;
    private final Integer ZCoordinate;
    // I probably need to change the public address over to a xumm request id of some sort. Then I can check the requestId
    // against my xumm server to make sure that the nft is legit
    private String worldName;

    public XLS19(String domain) {

        hash = domain;
        String[] character = domain.split(":");
        prefixFlag = character[0];
        issuanceNumber = character[1];
        coordinates = character[2];

        xCoordinate = Integer.parseInt(coordinates.substring(coordinates.indexOf('x') + 1, coordinates.indexOf('z')));
        XCoordinate = Integer.parseInt(coordinates.substring(coordinates.indexOf('X') + 1, coordinates.indexOf('Z')));
        zCoordinate = Integer.parseInt(coordinates.substring(coordinates.indexOf('z') + 1, coordinates.indexOf('X')));
        ZCoordinate = Integer.parseInt(coordinates.substring(coordinates.indexOf('Z') + 1));
    }

    public String getHash() {

        return hash;
    }

    public String getPrefixFlag() {

        return prefixFlag;
    }

    public String getIssuanceNumber() {

        return issuanceNumber;
    }

    public Integer getxCoordinate() {

        return xCoordinate;
    }

    public Integer getXCoordinate() {

        return XCoordinate;
    }

    public Integer getzCoordinate() {

        return zCoordinate;
    }

    public Integer getZCoordinate() {

        return ZCoordinate;
    }

    public String getWorldName() {

        return worldName;
    }

    public String convertToRegion() {

        return hash.toLowerCase().replace(":", "");
    }

}
