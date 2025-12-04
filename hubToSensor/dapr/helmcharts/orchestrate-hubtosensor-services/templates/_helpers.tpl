{{- define "flexibility-hub-simulator.fullname" -}}
{{ .Release.Name }}
{{- end -}}

{{- define "flexibility-hub-simulator.serviceName" -}}
{{ .Values.services.flexibilityHubSimulator.name }}
{{- end -}}

{{- define "flexibility-hub-simulator.pubsubName" -}}
{{ .Values.rabbitmq.pubsubName }}
{{- end -}}

{{- define "storageService.name" -}}
{{- default .Chart.Name .Values.services.storageService.name | trunc 63 | trimSuffix "-" -}}
{{- end -}}