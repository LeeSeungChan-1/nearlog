import { apiClient } from "../../api/apiClient";

export type UploadPurpose =
    "PROFILE" | "POST";

interface PresignResponse {
    uploadId: string;
    uploadUrl: string;
    method: "PUT";
    headers: Record<string, string>;
    expiresAt: string;
}

export async function uploadImage(
    file: File,
    purpose: UploadPurpose
): Promise<string> {

    const presignResponse =
        await apiClient.post<PresignResponse>(
            "/uploads/presign",
            {
                purpose,
                fileName: file.name,
                contentType: file.type,
                fileSize: file.size,
            }
        );

    const presign =
        presignResponse.data;

    const uploadResponse =
        await fetch(
            presign.uploadUrl,
            {
                method: "PUT",

                headers: {
                    "Content-Type":
                    file.type,
                },

                body: file,
            }
        );

    if (!uploadResponse.ok) {
        throw new Error(
            "S3 이미지 업로드에 실패했습니다."
        );
    }

    await apiClient.post(
        `/uploads/${presign.uploadId}/complete`
    );

    return presign.uploadId;
}