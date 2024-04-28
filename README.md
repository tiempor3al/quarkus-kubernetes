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
This a demo application that sends server sent events with the server date. 

So, what is so special? 

I just think that the [Mutiny](https://smallrye.io/smallrye-mutiny/latest/guides/broadcasting-to-multiple-subscribers/) library from Smallrye that makes this happen is just beautiful:

```java

    private Multi<String> ticks = Multi.createFrom().ticks().every(Duration.ofSeconds(1))
            .onItem().transform(tick -> LocalDateTime.now().toString())
            .onSubscription().invoke(() -> Log.info("Starting to emit ticks"))
            .onCancellation().invoke(() -> Log.info("No more ticks"))
            .broadcast()
            .withCancellationAfterLastSubscriberDeparture()
            .toAtLeast(1);

    @GET
    @Path("sse")
    @RestStreamElementType(MediaType.TEXT_PLAIN)
    public Multi<String> see() {
        Log.info("New subscriber");
        return ticks.onCancellation().invoke(() -> {
            Log.info("Removing subscriber");
        });
    }
```
That's it this all the code. It is so simple, and yet so powerful. It handles disconnections and stops broadcasting when there are no subscribers.


## How is this been deployed?

The releases are done by the tag-release.yaml workflow (check the .github/workflows directory), where the
template image is updated with the current version. The changes are pushed to the another [repository](https://github.com/tiempor3al/quarkus-kubernetes) so that ArgoCD
can pick the changes.

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

To apply the middleware on the default namespace:

```shell
 kubectl apply -f redirect.yaml
```
