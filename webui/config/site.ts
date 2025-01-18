export type SiteConfig = typeof siteConfig;

export const siteConfig = {
  name: "BeEyes",
  description: "Real-time monitoring of server operation and maintenance.",
  navItems: [
    {
      label: "仪表盘",
      href: "/",
    },
    {
      label: "终端",
      href: "/term",
    },
  ],
};
