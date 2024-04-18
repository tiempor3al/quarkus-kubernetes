# quarkus-kubernetes

This project uses Quarkus, the Supersonic Subatomic Java Framework.

If you want to learn more about Quarkus, please visit its website: https://quarkus.io/ .

## Running the application in dev mode

You can run your application in dev mode that enables live coding using:
```shell script
./mvnw compile quarkus:dev
```

> **_NOTE:_**  Quarkus now ships with a Dev UI, which is available in dev mode only at http://localhost:8080/q/dev/.

## Purpose of the application
This a demo application to test a CI/CD pipeline with ArgoCD. 

The releases are done by the tag-release.yaml workflow (check the .github/workflows directory), where the
template image is updated with the current version. The changes are pushed back to the "main" branch so that ArgoCD
can pick them up.

The docker image is pushed to https://hub.docker.com/repository/docker/tiempor3al/quarkus-kubernetes/general

The deployment file:
```yaml

apiVersion: apps/v1
kind: Deployment
metadata:
  name: quarkus-app
spec:
  replicas: 2
  selector:
    matchLabels:
      app: quarkus-app
  template:
    metadata:
      labels:
        app: quarkus-app
    spec:
      containers:
        - name: quarkus-app
          image: tiempor3al/quarkus-kubernetes:v20240418-043446
          ports:
            - containerPort: 8080
---
apiVersion: v1
kind: Service
metadata:
  name: quarkus-service
spec:
  type: LoadBalancer
  selector:
    app: quarkus-app
  ports:
    - protocol: TCP
      port: 8080
      targetPort: 8080
---
apiVersion: networking.k8s.io/v1
kind: Ingress
metadata:
  name: quarkus-ingress
  annotations:
    cert-manager.io/cluster-issuer: "cloudflare-dns-issuer"
    traefik.ingress.kubernetes.io/router.entrypoints: "web,websecure"
    traefik.ingress.kubernetes.io/router.middlewares: default-redirect-https@kubernetescrd
spec:
  ingressClassName: traefik
  tls:
    - hosts:
        - quarkus.enjambre.work
      secretName: quarkus-enjambre-workp-tls
  rules:
    - host: quarkus.enjambre.work
      http:
        paths:
          - path: /
            pathType: Prefix
            backend:
              service:
                name: quarkus-service
                port:
                  number: 8080
```

# About the annotations

The K3s cluster have a cert-manager configured with Cloudflare. Since Traefik, the default loadbalancer for K3s, does not automatically redirect http to https,
a CRD (Custom Resource Definition middleware file is necessary:

#### redirect.yaml
```yaml
apiVersion: traefik.containo.us/v1alpha1
kind: Middleware
metadata:
  name: redirect-https
  namespace: default
  labels:
    type: "redirect-https"
    creator: "tiempor3al"
spec:
  redirectScheme:
    scheme: https
    permanent: true
```

To apply the middleware (use the namespace where Traefik is running):

```shell
 kubectl apply -n kube-system -f redirect.yaml
```
