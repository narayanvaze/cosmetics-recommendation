# cosmetics-recommendation
In this project I have tried to build a web application which asks user to upload selfie or a portriat image and detects its skin tone.</br> 
Project is built in Java, Python and Angular. It also used Google Mediapipe API to to detect face portion in the image. 
Currently, the Java and Angular code is pushed on to the repo but the Python code is not available as of now. I will try to push it it as soon as possible.</br>
```bash
cosmetics-recommendation/cosmo-predictor-06
├── bin
│   ├── .gitignore
│   ├── .mvn
│   │   └── wrapper
│   │       ├── maven-wrapper.jar
│   │       └── maven-wrapper.properties
│   ├── mvnw
│   ├── mvnw.cmd
│   ├── pom.xml
│   └── src
│       └── main
│           └── resources
│               └── application.properties
├── colorBar.jpeg
├── Dockerfile
├── .gitignore
├── image-results
│   ├── face-1.jpeg
│   └── face-4.jpeg
├── inputFace.jpeg
├── input-images
│   ├── face-1.jpeg
│   └── face-4.jpeg
├── .mvn
│   └── wrapper
│       ├── maven-wrapper.jar
│       └── maven-wrapper.properties
├── mvnw
├── mvnw.cmd
├── pom.xml
├── resultImage.jpeg
├── src
│   └── main
│       ├── java
│       │   └── com
│       │       └── cosmopredictor
│       │           ├── controller
│       │           │   └── ImageUploadController.java
│       │           ├── db
│       │           │   └── ImageRepository.java
│       │           ├── ImageUploadApplication.java
│       │           └── model
│       │               └── ImageModel.java
│       └── resources
│           └── application.properties
└── test-images
    ├── cb_face-1.jpeg
    ├── face-1.jpeg
    ├── face-2.jpeg
    ├── face-3.jpeg
    └── face-4.jpeg

20 directories, 31 files
