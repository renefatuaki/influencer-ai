import { Alert, AlertTitle } from '@/components/ui/alert';
import { AlertCircle } from 'lucide-react';

export default function NotFound() {
  return (
    <div className="grid min-h-48 place-items-center">
      <Alert variant="destructive">
        <AlertCircle className="h-4 w-4" />
        <AlertTitle>No tweets found</AlertTitle>
      </Alert>
    </div>
  );
}
