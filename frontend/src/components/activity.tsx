import { Card, CardContent, CardFooter, CardTitle } from '@/components/ui/card';
import Link from 'next/link';
import { buttonVariants } from '@/components/ui/button';
import { ActivityProps } from '@/types';
import Image from 'next/image';
import icon from '@/assets/icons/x.svg';

export default function Activity({ text, link, createdAt }: Readonly<ActivityProps>) {
  const dateTime = new Date(createdAt);

  return (
    <Card className="grid lg:col-span-2 p-4 gap-2">
      <CardTitle className="grid grid-cols-2 gap-2">
        <Image src={icon} alt="Twitter Logo" unoptimized />
      </CardTitle>
      <CardContent>
        <p>{text}</p>
      </CardContent>
      <CardFooter className="grid lg:grid-cols-2 gap-2">
        <p className="text-xs">Created at: {dateTime.toUTCString()}</p>
        <Link href={link} className={buttonVariants({ variant: 'default', className: 'lg:col-start-2' })} target="_blank">
          See Post on Twitter
        </Link>
      </CardFooter>
    </Card>
  );
}
