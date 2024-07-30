import Link from 'next/link';
import {
  NavigationMenu,
  NavigationMenuItem,
  NavigationMenuLink,
  NavigationMenuList,
  navigationMenuTriggerStyle,
} from '@/components/ui/navigation-menu';

interface NavigationItem {
  title: string;
  href: string;
}

export default function Navigation() {
  const components: NavigationItem[] = [
    {
      title: 'Dashboard',
      href: '/dashboard',
    },
    {
      title: 'Bot Management',
      href: '/management',
    },
    {
      title: 'Approval',
      href: '/approval',
    },
    {
      title: 'Activity Log',
      href: '/activity',
    },
    {
      title: 'Settings',
      href: '/settings',
    },
  ];

  return (
    <>
      <NavigationMenu>
        <NavigationMenuList>
          {components.map(({ title, href }: NavigationItem) => (
            <NavigationMenuItem key={title}>
              <Link href={href} legacyBehavior passHref>
                <NavigationMenuLink className={navigationMenuTriggerStyle()}>{title}</NavigationMenuLink>
              </Link>
            </NavigationMenuItem>
          ))}
        </NavigationMenuList>
      </NavigationMenu>
    </>
  );
}
