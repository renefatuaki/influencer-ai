'use client';

import Link from 'next/link';
import { useRouter, useSearchParams } from 'next/navigation';
import { useState } from 'react';
import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle, DialogTrigger } from '@/components/ui/dialog';
import { Button, buttonVariants } from '@/components/ui/button';
import { POST } from '@/lib/fetch';
import AlertResponse from '@/components/alert-response';

export default function TwitterAuth({ url }: { url: string }) {
  const searchParams = useSearchParams();
  const code = searchParams.get('code');
  const state = searchParams.get('state');
  const [open, setOpen] = useState(!!code);
  const [message, setMessage] = useState('');
  const [error, setError] = useState(false);
  const router = useRouter();

  const clickHandler = async () => {
    const response = await POST('/twitter', { code, state });

    if (!response.data) {
      setMessage(response.message);
      setError(true);
    } else {
      router.push(`/management/${response.data.id}`);
    }
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button variant="outline">Add Influencer</Button>
      </DialogTrigger>
      {!code ? (
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Authorize Twitter Access</DialogTitle>
            <DialogDescription>This action will authorize this application to access your twitter account.</DialogDescription>
          </DialogHeader>
          <DialogFooter>
            <Link href={url} className={buttonVariants({ variant: 'outline' })}>
              Authorize X
            </Link>
          </DialogFooter>
        </DialogContent>
      ) : (
        <DialogContent>
          <DialogHeader>
            <DialogTitle>Connect Twitter Account</DialogTitle>
            <DialogDescription>
              This action will permanently add your Twitter account to our servers and cannot be undone.
            </DialogDescription>
          </DialogHeader>
          <DialogFooter className="flex flex-wrap gap-4">
            {error ? (
              <>
                <AlertResponse error={true} message={message} />
                <Link href={url} className={buttonVariants({ variant: 'outline' })}>
                  Restart Authorization
                </Link>
              </>
            ) : (
              <Button variant="outline" onClick={clickHandler}>
                Connect
              </Button>
            )}
          </DialogFooter>
        </DialogContent>
      )}
    </Dialog>
  );
}
