import { Card, CardContent } from '@/components/ui/card';
import Image from 'next/image';
import { Button } from '@/components/ui/button';
import { RotateCw } from 'lucide-react';
import { useState } from 'react';
import { updateTwitterBaseImage } from '@/actions';
import Loading from '@/components/loading';

export default function BaseImage({ twitterId }: Readonly<{ twitterId: string }>) {
  const url = `${process.env.NEXT_PUBLIC_API}/stability/base-image/${twitterId}`;
  const [isLoading, setIsLoading] = useState(false);
  const [image, setImage] = useState(`${url}?${Date.now()}`);

  const clickHandler = () => {
    setIsLoading(true);
    updateTwitterBaseImage(twitterId).then(() => {
      setIsLoading(false);
      setImage(`${url}?${Date.now()}`);
    });
  };

  return (
    <Card className="p-4">
      <CardContent className="relative min-h-96 flex justify-center items-center">
        {isLoading ? (
          <>
            <Loading />
          </>
        ) : (
          <>
            <Image className="rounded-md object-cover" src={image} alt="Influencer Image" fill={true} />
            <Button onClick={clickHandler} type="button" variant="secondary" className="absolute bottom-0 right-0 m-4 rounded-full p-0">
              <RotateCw className="m-2" />
            </Button>
          </>
        )}
      </CardContent>
    </Card>
  );
}
