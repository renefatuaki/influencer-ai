import {Card, CardContent, CardDescription, CardFooter, CardHeader, CardTitle} from '@/components/ui/card';
import {SubmitButton} from '@/components/submit-button';
import {useFormState} from 'react-dom';
import {ReactNode} from 'react';

type EditorType = {
    influencerId: string;
    title: string;
    description: string;
    children?: ReactNode;
    action: (prevState: any, formData: FormData) => Promise<{ error: boolean; message: string }>;
};

export default function Editor({influencerId, title, description, children, action}: Readonly<EditorType>) {
    const [state, formAction] = useFormState(action, {
        error: false,
        message: '',
    });

    return (
        <form action={formAction}>
            <input type="hidden" name="id" value={influencerId} readOnly={true}/>
            <Card>
                <CardHeader>
                    <CardTitle>{title}</CardTitle>
                    <CardDescription>{description}</CardDescription>
                </CardHeader>
                <CardContent>{children}</CardContent>
                <CardFooter>
                    <SubmitButton state={state}>Update</SubmitButton>
                </CardFooter>
            </Card>
        </form>
    );
}
