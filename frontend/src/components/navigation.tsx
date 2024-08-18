import Link from 'next/link';
import { NavigationMenu, NavigationMenuItem, NavigationMenuLink, NavigationMenuList, navigationMenuTriggerStyle } from '@/components/ui/navigation-menu';

type NavigationItem = {
  title: string;
  href: string;
};

export default function Navigation() {
  const components: NavigationItem[] = [
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
  ];

  return (
    <>
      <NavigationMenu className="my-4 flex ">
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
      <h1 className="font-mono font-bold text-lg">Influencer AI</h1>
    </>
  );
}
