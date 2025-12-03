{{/*
Expand the name of the chart.
*/}}
{{- define "orchestrate-hubtosensor-services.name" -}}
{{- default .Chart.Name .Values.nameOverride | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Create a default fully qualified app name.
*/}}
{{- define "orchestrate-hubtosensor-services.fullname" -}}
{{- if .Values.fullnameOverride }}
{{- .Values.fullnameOverride | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- $name := default .Chart.Name .Values.nameOverride }}
{{- if contains $name .Release.Name }}
{{- .Release.Name | trunc 63 | trimSuffix "-" }}
{{- else }}
{{- printf "%s-%s" .Release.Name $name | trunc 63 | trimSuffix "-" }}
{{- end }}
{{- end }}
{{- end }}

{{/*
Create chart name and version as used by the chart label.
*/}}
{{- define "orchestrate-hubtosensor-services.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" }}
{{- end }}

{{/*
Common labels
*/}}
{{- define "orchestrate-hubtosensor-services.labels" -}}
helm.sh/chart: {{ include "orchestrate-hubtosensor-services.chart" . }}
{{ include "orchestrate-hubtosensor-services.selectorLabels" . }}
{{- if .Chart.AppVersion }}
app.kubernetes.io/version: {{ .Chart.AppVersion | quote }}
{{- end }}
app.kubernetes.io/managed-by: {{ .Release.Service }}
{{- end }}

{{/*
Selector labels
*/}}
{{- define "orchestrate-hubtosensor-services.selectorLabels" -}}
app.kubernetes.io/name: {{ include "orchestrate-hubtosensor-services.name" . }}
app.kubernetes.io/instance: {{ .Release.Name }}
{{- end }}

{{/*
Create the name of the service account to use
*/}}
{{- define "orchestrate-hubtosensor-services.serviceAccountName" -}}
{{- if .Values.serviceAccount.create }}
{{- default (include "orchestrate-hubtosensor-services.fullname" .) .Values.serviceAccount.name }}
{{- else }}
{{- default "default" .Values.serviceAccount.name }}
{{- end }}
{{- end }}

# --- Service-Specific Name Helpers ---

{{- define "flexibilityHubSimulator.name" -}}
{{- .Values.services.flexibilityHubSimulator.name -}}
{{- end }}

{{- define "flexibilityBridge.name" -}}
{{- .Values.services.flexibilityBridge.name -}}
{{- end }}

{{- define "protocolAdapter.name" -}}
{{- .Values.services.protocolAdapter.name -}}
{{- end }}

{{- define "storageService.name" -}}
{{- .Values.services.storageService.name -}}
{{- end }}

{{- define "hesSimulator.name" -}}
{{- .Values.services.hesSimulator.name -}}
{{- end }}

{{/*
Service Name: dataApi
*/}}
{{- define "dataApi.name" -}}
{{- .Values.dataApi.name -}}
{{- end }}

{{/*
Service Name: uiApp
*/}}
{{- define "uiApp.name" -}}
{{- .Values.uiApp.name -}}
{{- end }}
