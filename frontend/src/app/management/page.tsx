import { getAuthorizationUrl } from '@/lib/auth';
import AddAccount from '@/components/AddAccount';
import { DataTable } from '@/components/table/DataTable';
import { columns, TableData } from '@/components/table/Columns';

export default function Management() {
  const authorizationUrl = getAuthorizationUrl();

  return (
    <>
      <div className="container flex flex-col mx-auto py-10 gap-4">
        <AddAccount url={authorizationUrl}></AddAccount>
        <DataTable columns={columns} />
      </div>
    </>
  );
}
