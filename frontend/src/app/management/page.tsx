import { getAuthorizationUrl } from '@/lib/auth';
import AddAccount from '@/components/AddAccount';

export default function Management() {
  const authorizationUrl = getAuthorizationUrl();

  return (
    <>
      <AddAccount url={authorizationUrl}></AddAccount>
    </>
  );
}
