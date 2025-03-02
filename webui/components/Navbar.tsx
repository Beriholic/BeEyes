"use client";
import {
  Navbar as HeroUINavbar,
  NavbarContent,
  NavbarBrand,
  NavbarItem,
  NavbarMenuToggle,
  NavbarMenu,
} from "@heroui/navbar";
import NextLink from "next/link";
import {
  Avatar,
  Dropdown,
  DropdownItem,
  DropdownMenu,
  DropdownTrigger,
} from "@heroui/react";

import { siteConfig } from "@/config/site";
import { ThemeSwitch } from "@/components/theme-switch";
import { Logo } from "@/components/icons";
import { api } from "@/api/instance";
import { PopMsg } from "@/store/pops";
import { useRouter } from "next/navigation";
import { useUserInfoStore } from "@/store/user";

export const Navbar = () => {
  const router = useRouter();

  const userInfo = useUserInfoStore((state) => state.userInfo);

  const logout = async () => {
    const res = await api.authService.logout();
    if (res.code !== 200) {
      PopMsg({
        type: "danger",
        title: "登出失败",
        description: res.message,
      });
    }

    PopMsg({
      type: "success",
      title: "登出成功",
      description: "正在跳转至登录页",
    });

    setTimeout(() => {
      router.push("/login");
    }, 600);
  };

  const goto = (url: string) => {
    router.push(url);
  };
  return (
    <HeroUINavbar maxWidth="2xl" position="sticky">
      <NavbarContent className="basis-1/5 sm:basis-full" justify="start">
        <NavbarBrand as="li" className="gap-3 max-w-fit">
          <NextLink className="flex justify-start items-center gap-1" href="/">
            <Logo className="size-6" />
            <p className="font-bold text-inherit">BeEyes</p>
          </NextLink>
        </NavbarBrand>
        <ul className="hidden sm:flex gap-4 justify-start ml-2">
          {siteConfig.navItems.map((item) => (
            <NavbarItem key={item.href}>
              <NextLink color="foreground" href={item.href}>
                {item.label}
              </NextLink>
            </NavbarItem>
          ))}
        </ul>
        <NavbarMenuToggle className="sm:hidden" />
        <NavbarMenu>
          {siteConfig.navItems.map((item) => (
            <NavbarItem key={item.href}>
              <NextLink color="foreground" href={item.href}>
                {item.label}
              </NextLink>
            </NavbarItem>
          ))}
        </NavbarMenu>
      </NavbarContent>

      <NavbarContent as="div" className="basis-1/5 sm:basis-full" justify="end">
        <ThemeSwitch />
        <Dropdown placement="bottom-end">
          <DropdownTrigger>
            <Avatar
              isBordered
              as="button"
              className="transition-transform"
              color="secondary"
              name={userInfo.username}
              size="sm"
              src={userInfo.avatar}
            />
          </DropdownTrigger>
          <DropdownMenu aria-label="Profile Actions" variant="flat">
            <DropdownItem key="profile" className="h-14 gap-2">
              <p className="font-semibold">登陆于</p>
              <p className="font-semibold">{userInfo.username}</p>
            </DropdownItem>
            <DropdownItem key="settings" onPress={() => goto("/setting")}>
              设置
            </DropdownItem>
            <DropdownItem key="logout" color="danger" onPress={logout}>
              登出
            </DropdownItem>
          </DropdownMenu>
        </Dropdown>
      </NavbarContent>
    </HeroUINavbar>
  );
};
