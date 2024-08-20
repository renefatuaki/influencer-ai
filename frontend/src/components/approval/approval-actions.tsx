'use client';

import { Button } from '@/components/ui/button';
import { CardFooter } from '@/components/ui/card';
import { approveTweet, retryImageGeneration } from '@/actions';
import { useToast } from '@/components/ui/use-toast';
import Loading from '@/components/loading';
import { useState } from 'react';

export default function ApprovalActions({ id }: { readonly id: string }) {
  const { toast } = useToast();
  const [isLoading, setIsLoading] = useState(false);

  const approveHandler = () => {
    setIsLoading(true);

    approveTweet(id).then(({ status, link }) => {
      const error = status > 299;

      setIsLoading(false);

      toast({
        variant: error ? 'destructive' : 'default',
        title: error ? 'Error' : 'Success',
        description: error ? (
          <p>Approval hit a snag. Please try again.</p>
        ) : (
          <p>
            Your tweet has been approved and is now live! <a href={link}>See the post.</a>
          </p>
        ),
      });
    });
  };

  const retryHandler = () => {
    setIsLoading(true);

    retryImageGeneration(id).then(({ status }) => {
      const error = status > 299;

      setIsLoading(false);

      toast({
        variant: error ? 'destructive' : 'default',
        title: error ? 'Error' : 'Success',
        description: error ? <p>Something went wrong. Give it another shot later.</p> : <p>New image generated! Awaiting your approval.</p>,
      });
    });
  };

  return (
    <CardFooter className="grid lg:col-span-6 grid-cols-6 gap-4">
      <Button className="col-span-3" onClick={retryHandler} disabled={isLoading}>
        Retry
      </Button>
      <Button className="col-span-3" onClick={approveHandler} disabled={isLoading}>
        Approve
      </Button>
      <div className="col-span-6 flex justify-center">{isLoading && <Loading />}</div>
    </CardFooter>
  );
}
