{{- define "storageService.name" -}}
{{- default .Chart.Name .Values.services.storageService.name | trunc 63 | trimSuffix "-" -}}
{{- end -}}