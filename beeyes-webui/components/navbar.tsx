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

export const Navbar = () => {
  return (
    <HeroUINavbar maxWidth="xl" position="sticky">
      <NavbarContent className="basis-1/5 sm:basis-full" justify="start">
        <NavbarBrand as="li" className="gap-3 max-w-fit">
          <NextLink className="flex justify-start items-center gap-1" href="/">
            <Logo />
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
              name="Jason Hughes"
              size="sm"
              src="https://avatars.githubusercontent.com/u/96678707?v=4&size=64"
            />
          </DropdownTrigger>
          <DropdownMenu aria-label="Profile Actions" variant="flat">
            <DropdownItem key="profile" className="h-14 gap-2">
              <p className="font-semibold">Signed in as</p>
              <p className="font-semibold">beriholic@petalmail.com</p>
            </DropdownItem>
            <DropdownItem key="settings">My Settings</DropdownItem>
            <DropdownItem key="team_settings">Team Settings</DropdownItem>
            <DropdownItem key="analytics">Analytics</DropdownItem>
            <DropdownItem key="system">System</DropdownItem>
            <DropdownItem key="configurations">Configurations</DropdownItem>
            <DropdownItem key="help_and_feedback">Help & Feedback</DropdownItem>
            <DropdownItem key="logout" color="danger">
              Log Out
            </DropdownItem>
          </DropdownMenu>
        </Dropdown>
      </NavbarContent>
    </HeroUINavbar>
  );
};
