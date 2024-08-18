import Image from 'next/image';
import { Suspense } from 'react';
import { getUnapprovedTweet } from '@/actions';
import { Card, CardContent, CardTitle } from '@/components/ui/card';
import icon from '@/assets/icons/x.svg';
import ApprovalActions from '@/components/approval/approval-actions';

const UnapprovedTweet = async () => {
  const unapprovedTweets = await getUnapprovedTweet();

  return (
    <>
      {unapprovedTweets.map((tweet) => (
        <Card key={tweet.id} className="grid lg:col-span-6 p-4 gap-4">
          <CardTitle className="grid lg:grid-cols-2 gap-2">
            <Image src={icon} alt="Twitter Logo" unoptimized />
          </CardTitle>
          <CardContent key={tweet.id} className="grid lg:col-span-6 gap-4">
            <p>{tweet.text}</p>
            <div className="relative min-h-72 md:min-h-96 flex justify-center items-center">
              <Image
                className="rounded-md object-cover"
                src={`${process.env.API}/twitter/tweet/image/${tweet.imageId}`}
                alt="Unapproved Tweet Image"
                fill={true}
              />
            </div>
          </CardContent>
          <ApprovalActions id={tweet.id} />
        </Card>
      ))}
    </>
  );
};

export default function ApprovalPage() {
  return (
    <div className="grid lg:grid-cols-12 gap-4">
      <Suspense fallback={<div>Loading...</div>}>
        <UnapprovedTweet />
      </Suspense>
    </div>
  );
}
