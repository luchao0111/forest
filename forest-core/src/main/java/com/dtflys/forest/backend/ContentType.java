package com.dtflys.forest.backend;

import com.dtflys.forest.utils.StringUtils;

/**
 * @author gongjun[dt_flys@hotmail.com]
 * @since 2020-08-05 23:42
 */
public class ContentType {

    private final String type;

    private final String subType;

    private String charset;

    public ContentType(String type, String subType) {
        this.type = type;
        this.subType = subType;
    }

    public ContentType(String contentType) {
        String[] group = contentType.split(";");
        String cty = group[0].trim();
        String[] strs = cty.split("/");
        this.type = strs[0];
        if (strs.length > 1) {
            this.subType = strs[1];
        } else {
            this.subType = null;
        }
        if (group.length > 1) {
            String chartExpr = group[1];
            String[] expr = chartExpr.split("=");
            if ("charset".equalsIgnoreCase(expr[0].trim())
                    && expr.length > 1) {
                this.charset = expr[1].trim();
            }
        }
    }

    public String getType() {
        return type;
    }

    public String getSubType() {
        return subType;
    }

    public String getCharset() {
        return charset;
    }

    public boolean isEmpty() {
        return StringUtils.isEmpty(type) && StringUtils.isEmpty(subType);
    }

    public boolean isApplication() {
        return "application".equals(type);
    }

    public boolean isJson() {
        if (subType == null) {
            return false;
        }
        return "json".equals(subType);
    }

    public boolean isXml() {
        if (subType == null) {
            return false;
        }
        return subType.contains("xml");
    }

    public boolean isZip() {
        if (subType == null) {
            return false;
        }
        return "zip".equals(subType);
    }

    public boolean isJavaScript() {
        if (subType == null) {
            return false;
        }
        return "javascript".equals(subType);
    }

    public boolean isOctetStream() {
        if (subType == null) {
            return false;
        }
        return "octet-stream".equals(subType);
    }

    public boolean isOgg() {
        if (subType == null) {
            return false;
        }
        return "ogg".equals(subType);
    }

    public boolean isPdf() {
        if (subType == null) {
            return false;
        }
        return "pdf".equals(subType);
    }


    public boolean isText() {
        return "text".equals(type);
    }

    public boolean isAudio() {
        return "audio".equals(type);
    }

    public boolean isImage() {
        return "image".equals(type);
    }

    public boolean isMultipart() {
        return "multipart".equals(type);
    }

    public boolean isVideo() {
        return "video".equals(type);
    }

    public boolean canReadAsString() {
        return isJson() || isXml() || isJavaScript() || isText();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(type);
        if (StringUtils.isNotEmpty(subType)) {
            builder.append("/").append(subType);
        }
        return builder.toString();
    }
}