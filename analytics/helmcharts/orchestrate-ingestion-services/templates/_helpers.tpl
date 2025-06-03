{{- define "ingestion-service.name" -}}
ingestion-service
{{- end }}

{{- define "ingestion-service.fullname" -}}
{{ .Release.Name }}-{{ include "ingestion-service.name" . }}
{{- end }}
