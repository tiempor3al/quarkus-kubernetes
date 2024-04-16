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
The goal of the application is to test a CI/CD pipeline using GitHub Actions. 

The application will be deployed to a K3s Kubernetes cluster hosted on Hetzner using a master control plane and two agent nodes. 

On K3s, ArgoCD is used to deploy the application.

You can check the application running at [https://quarkus.enjambre.work](https://quarkus.enjambre.work).



