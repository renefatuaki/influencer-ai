'use client';

import { Dialog, DialogContent, DialogDescription, DialogFooter, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { Button } from '@/components/ui/button';
import { DropdownMenuItem } from '@/components/ui/dropdown-menu';
import { MouseEvent, useState } from 'react';
import { DELETE } from '@/lib/fetch';

export default function DeleteDialog({ id }: { readonly id: string }) {
  const [open, setOpen] = useState(false);

  const handleClick = (event: MouseEvent) => {
    event.preventDefault();
    setOpen(true);
  };

  const handleDelete = () => {
    DELETE(`/influencer/${id}`).then(() => {
      setOpen(false);
      window.location.reload();
    });
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DropdownMenuItem onClick={handleClick}>Delete</DropdownMenuItem>
      <DialogContent>
        <DialogHeader>
          <DialogTitle>Delete Confirmation</DialogTitle>
          <DialogDescription>
            Are you sure you want to delete this influencer? This action cannot be undone and you will lose all data associated with this influencer.
          </DialogDescription>
        </DialogHeader>
        <DialogFooter>
          <Button variant={'destructive'} onClick={handleDelete}>
            Delete
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
