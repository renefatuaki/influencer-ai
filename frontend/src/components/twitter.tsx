import { Input } from '@/components/ui/input';
import { Twitter } from '@/types';
import { Label } from '@/components/ui/label';
import { Card, CardContent, CardHeader, CardTitle } from '@/components/ui/card';

type TwitterDetailsProps = {
  data: Twitter;
};

export default function TwitterDetails({ data }: Readonly<TwitterDetailsProps>) {
  return (
    <Card>
      <CardHeader>
        <CardTitle>Twitter</CardTitle>
      </CardHeader>
      <CardContent className="grid lg:grid-cols-2 gap-4">
        <div>
          <Label htmlFor="id">ID</Label>
          <Input type="text" id="id" placeholder={data.id} disabled />
        </div>
        <div>
          <Label htmlFor="name">Name</Label>
          <Input type="text" id="name" placeholder={data.name} disabled />
        </div>
        <div>
          <Label htmlFor="username">Username</Label>
          <Input type="text" id="username" placeholder={data.username} disabled />
        </div>
        <div>
          <Label htmlFor="isAuthorized">Is authorized?</Label>
          <Input type="text" id="isAuthorized" placeholder={data.auth.isAuthorized ? 'Authorized' : 'Unauthorized'} disabled />
        </div>
      </CardContent>
    </Card>
  );
}
