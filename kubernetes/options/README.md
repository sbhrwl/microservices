# Publishing Docker images
- [Push Docker image to a container registry](#push-docker-image-to-a-container-registry)
  - [Create GitHub Personal Access Token for pushing Docker images to GitHub Container Registry](#create-github-personal-access-token-for-pushing-docker-images-to-gitHub-container-registry)
  - [Using PAT to login to GHCR](#using-pat-to-login-to-ghcr)
  - [Push image](#push-image)
- [Othe options](#other-options)
  - [Using a local Docker image with minikube](#using-a-local-docker-image-with-minikube)
  - [Use kind](#use-kind)
  - [Use a private registry](#use-a-private-registry)
## Push Docker image to a container registry
- As Kubernetes pulls images from a container registry, we will use `GHCR`
### Create GitHub Personal Access Token for pushing Docker images to GitHub Container Registry
- Go to GitHub: [Developer settings](https://github.com/settings/tokens)
- Click: “Fine-grained tokens” or “Personal access tokens (classic)”
  - For simplicity, choose "Personal access tokens (classic)".
  - Click “Generate new token” → “Generate new token (classic)”.
- Fill in details:
  - Name: e.g., `GHCR Docker Push`
  - Expiration: Set as needed (e.g., 30 or 90 days).
  - Scopes:
    - Under `repo`: Check if you're pushing from private repos.
    - Under `write:packages`: Required to push images.
    - Optionally: `read:packages` if pulling.
- Click “Generate token”
- Copy the token now 
  - It won’t be shown again.
### Using PAT to login to GHCR
- In your terminal:
```bash
echo YOUR_TOKEN_HERE | docker login ghcr.io -u YOUR_GITHUB_USERNAME --password-stdin
```
- Log in first with `docker login` if needed.
### Push image
```bash
docker push your-username/your-image:tag
```

- Make the package public
  - Go to https://github.com/your-username.
  - Click "Packages" on your profile (or repo if image is published via repo).
  - Click the image/package (e.g., my-app).
  - Click "Package Settings" (gear icon or settings tab).
  - Scroll to "Package visibility".
  - Change visibility to Public.
- Kubernetes can now pull your image without a secret.
## Other options
- If your image is local and not pushed to a registry, `Kubernetes` won’t find it.
- You need to
  - Push it to Docker Hub, `GitHub Container Registry`, or a private registry like GCR, ECR, etc. OR
  - Use tools like `kind` or `minikube` which can `load local images` for testing.
### Using a local Docker image with minikube
- Recommended for local development
- Enable Docker environment in `minikube`
   ```bash
   eval $(minikube docker-env)
   ```
- **Build your Docker image locally**
   ```bash
   docker build -t my-local-image:latest /path/to/your/folder
   ```
- Reference it in your Kubernetes manifest
   ```yaml
   spec:
     containers:
     - name: my-app
       image: my-local-image:latest
   ```
- **Apply the manifest**
   ```bash
   kubectl apply -f your-deployment.yaml
   ```
### Use kind
- Kubernetes in Docker
  - If you're using `kind`, load the image into the cluster:
- **Build your image:**
   ```bash
   docker build -t my-local-image:latest /path/to/your/folder
   ```
- **Load it into kind:**
   ```bash
   kind load docker-image my-local-image:latest
   ```
- Use it in your manifest (same as above)
### Use a private registry
- For real clusters
- **Build and tag your image:**
   ```bash
   docker build -t yourregistry.com/my-app:latest /path/to/your/folder
   ```
- **Push to the registry:**
   ```bash
   docker push yourregistry.com/my-app:latest
   ```
- **Create a secret in k8s if needed, then update your manifest to use the image from the registry.**
