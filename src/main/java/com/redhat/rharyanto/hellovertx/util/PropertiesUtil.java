package com.redhat.rharyanto.hellovertx.util;

import io.vertx.core.impl.logging.Logger;
import io.vertx.core.impl.logging.LoggerFactory;
import io.vertx.core.json.JsonObject;

/**
 * @author <a href="mailto:rharyant@redhat.com">Robertus Lilik Haryanto</a>
 */
public class PropertiesUtil {

  private static final Logger logger = LoggerFactory.getLogger(PropertiesUtil.class);

  public static String getConfig(JsonObject jsonObject, String key, String def) {
    if (key.contains(".")) {
      String innerKey = key.substring(0, key.indexOf("."));
      String remaining = key.substring(key.indexOf(".") + 1);

      if (jsonObject.containsKey(innerKey)) {
        return getConfig(jsonObject.getJsonObject(innerKey), remaining, def);
      } else {
        return null;
      }
    } else {
      Object valueObj = jsonObject.getValue(key, def);
      return (valueObj != null) ? String.valueOf(valueObj) : null;
    }
  }

  public static Integer getConfigAsInteger(JsonObject jsonObject, String key, String def) {
    return Integer.parseInt(getConfig(jsonObject, key, def));
  }
}
