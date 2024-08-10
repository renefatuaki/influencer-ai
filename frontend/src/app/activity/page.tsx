import { GET } from '@/lib/fetch';
import { notFound } from 'next/navigation';
import { Suspense } from 'react';
import Activity from '@/components/activity';
import { ActivityProps } from '@/types';

async function Activities() {
  const tweets: ActivityProps[] = await GET('/twitter/tweets', 'no-cache');

  if (!tweets) notFound();

  tweets.sort((a, b) => (new Date(a.createdAt) > new Date(b.createdAt) ? -1 : 1));

  return tweets.map((tweet) => <Activity key={tweet.id} id={tweet.id} text={tweet.text} link={tweet.link} createdAt={tweet.createdAt} />);
}

export default function ActivityLog() {
  return (
    <div className="grid lg:grid-cols-4 gap-4">
      <Suspense>
        <Activities />
      </Suspense>
    </div>
  );
}
