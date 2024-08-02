'use client';

import { useEffect, useState } from 'react';
import PersonalityCard from '@/components/attributes/personality-card';
import AppearanceCard from '@/components/attributes/appearance-card';
import { Influencer } from '@/types';
import { GET } from '@/lib/fetch';

type ParamsProps = {
  params: {
    id: string;
  };
};

export default function InfluencerSettings({ params }: ParamsProps) {
  const { id } = params;
  const [influencer, setInfluencer] = useState<Influencer>();

  useEffect(() => {
    GET(`/influencer/${id}`, 'no-cache').then((data) => setInfluencer(data));
  }, [id]);

  return (
    <>
      <div className="flex">
        <div className="grid m-4">{influencer?.personality && <PersonalityCard data={influencer?.personality} id={id} />}</div>
        <div className="grid m-4">{influencer?.appearance && <AppearanceCard data={influencer?.appearance} id={id} />}</div>
      </div>
    </>
  );
}
