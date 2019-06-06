package com.github.hornta.trollskogen;

public enum ServerVersion {
  v1_13_R1("1.13", "1.13-R1", 13, new String[] { "1.13" }),
  v1_13_R2("1.13", "1.13-R2", 13, new String[] { "1.13.1", "1.13.2" }),
  v1_14_R1("1.14", "1.14-R1", 14, new String[] { "1.14" });

  private String name;
  private String detailName;
  private int currentVersionNumber;
  private String[] spigotReleases;
  private static ServerVersion serverVersion;

  ServerVersion(String name, String detailName, int currentVersionNumber, String[] spigotReleases) {
    this.name = name;
    this.detailName = detailName;
    this.currentVersionNumber = currentVersionNumber;
    this.spigotReleases = spigotReleases.clone();
  }

  public String getName() {
    return this.name;
  }

  public String getDetailName() {
    return this.detailName;
  }

  public int getCurrentVersionNumber() {
    return this.currentVersionNumber;
  }

  public String[] getSpigotReleases() {
    return this.spigotReleases;
  }

  public static ServerVersion getServerVersion() {
    return serverVersion;
  }

  public static void setServerVersion(ServerVersion arg0) {
    serverVersion = arg0;
  }

  public static ServerVersion valueOfSpigotRelease(String arg0) {
    ServerVersion[] arrayOfServerVersion;
    int j = (arrayOfServerVersion = values()).length;
    for (int i = 0; i < j; i++)
    {
      ServerVersion version = arrayOfServerVersion[i];
      String[] arrayOfString;
      int m = (arrayOfString = version.getSpigotReleases()).length;
      for (int k = 0; k < m; k++)
      {
        String release = arrayOfString[k];
        if (release.equalsIgnoreCase(arg0)) {
          return version;
        }
      }
    }
    return null;
  }
}
