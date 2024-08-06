import { Alert, AlertDescription, AlertTitle } from '@/components/ui/alert';
import { AlertCircle, CircleCheckBig } from 'lucide-react';

export type Response = {
  message: string;
  error: boolean;
  link?: string;
};

export default function AlertResponse({ error, message, link }: Response) {
  if (message === '') return null;

  return (
    <>
      {error ? (
        <Alert variant="destructive">
          <AlertCircle className="h-4 w-4" />
          <AlertTitle>Error</AlertTitle>
          <AlertDescription>{message}</AlertDescription>
        </Alert>
      ) : (
        <Alert variant="default">
          <CircleCheckBig className="h-4 w-4 stroke-green-700" />
          <AlertTitle className="text-green-700">Success</AlertTitle>
          <AlertDescription className="text-green-700">
            <a href={link}>{message}</a>
          </AlertDescription>
        </Alert>
      )}
    </>
  );
}
