import { getAuthorizationUrl } from '@/lib/auth';
import TwitterAuth from '@/components/twitter-auth';
import { TwitterTable } from '@/components/table/twitter-table';
import { columns } from '@/components/table/columns';

export default function Management() {
  const authorizationUrl = getAuthorizationUrl();

  return (
    <>
      <div className="flex flex-col mx-auto py-10 gap-4">
        <TwitterAuth url={authorizationUrl}></TwitterAuth>
        <TwitterTable columns={columns} />
      </div>
    </>
  );
}
