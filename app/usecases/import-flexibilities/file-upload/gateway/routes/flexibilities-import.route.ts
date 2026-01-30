import type { MultipartFile, MultipartValue } from "@fastify/multipart";
import type { FastifyReply, FastifyRequest } from "fastify";
import {
  UploadCsvRequest,
  UploadCsvResponse,
} from "../__generated__/core/api/flexibility/v1/flexibility.js";
import { FlexibilityClient } from "../clients/flexibility-client.js";

const createFileRequest = async (
  data: MultipartFile,
): Promise<UploadCsvRequest> => {
  const orgCode = (data.fields?.orgCode as MultipartValue)?.value;
  const metadata = JSON.stringify({ mimetype: data.mimetype, orgCode });
  const buf = await data.toBuffer();

  return UploadCsvRequest.create({
    metadata: metadata,
    content: buf,
    filename: data.filename || "",
  });
};

// Test with curl:
// curl -X POST http://localhost:4000/api/flexibilities/import -F file=@Flexibilities-L540.csv
export const flexibilitiesImport = async (
  request: FastifyRequest,
  reply: FastifyReply,
) => {
  const data = await request.file();

  if (!data) {
    return reply.code(400).send({ error: "Invalid data." });
  }

  try {
    const client = new FlexibilityClient();

    const fileReq = await createFileRequest(data as MultipartFile);

    // Call the gRPC service using the client pattern
    const response = await client.uploadCsv(
      fileReq,
      request.headers.authorization as string,
    );

    console.log(
      `Uploaded flexibilities file: ${data.filename}, ${data.mimetype}`,
    );

    return reply.code(200).send(UploadCsvResponse.toJSON(response));
  } catch (err: any) {
    console.error("uploadFlexibilities error:", err);
    return reply.code(500).send({ error: err?.message || "upload failed" });
  }
};

export default flexibilitiesImport;
