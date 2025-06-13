{{/*
helpers.tpl for orchestrate-ingestion-grpc-services chart
Contains helper templates for ingestion-grpc-service and hub-service
*/}}

{{/*
Generate a name for ingestion-grpc-service
*/}}
{{- define "ingestion-grpc-service.name" -}}
ingestion-grpc-service
{{- end }}

{{/*
Generate a fullname for ingestion-grpc-service
*/}}
{{- define "ingestion-grpc-service.fullname" -}}
{{- printf "%s-ingestion-grpc-service" .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{/*
Generate labels for ingestion-grpc-service
*/}}
{{- define "ingestion-grpc-service.labels" -}}
app.kubernetes.io/name: ingestion-grpc-service
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/version: {{ .Chart.AppVersion | default "0.1.0" }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Generate selector labels for ingestion-grpc-service
*/}}
{{- define "ingestion-grpc-service.selectorLabels" -}}
app.kubernetes.io/name: ingestion-grpc-service
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Generate a name for hub-service
*/}}
{{- define "hub-service.name" -}}
hub-service
{{- end }}

{{/*
Generate a fullname for hub-service
*/}}
{{- define "hub-service.fullname" -}}
{{- printf "%s-hub-service" .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- end }}

{{/*
Generate labels for hub-service
*/}}
{{- define "hub-service.labels" -}}
app.kubernetes.io/name: hub-service
app.kubernetes.io/instance: {{ .Release.Name }}
app.kubernetes.io/version: {{ .Chart.AppVersion | default "0.1.0" }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Generate selector labels for hub-service
*/}}
{{- define "hub-service.selectorLabels" -}}
app.kubernetes.io/name: hub-service
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}
