# MyNetty
>By imitating spring mvc, a layer of encapsulation of netty, 
so that netty can process various requests accordingly 
through the mapping in the controller like spring mvc, 
decoupling the processing logic of the business.

## Comenzando 🚀

***pom.xml***
```xml
<!--    MyNetty build and installed in local    -->
<dependency>
    <groupId>com.juno.framework</groupId>
    <artifactId>my-netty</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
<!--    MyNetty dependency  lombok   -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <optional>true</optional>
</dependency>
```
***application.yml***
```yml
my-netty:
  port: 8090
  boss-thread: 2
  worker-thread: 2
  keepalive: true
  backlog: 100
  channel-pool-size: 100
  scan-package: com.juno.mynetty.controller  # controller package path
```
***Application***
> the annotation of EnableMyNettyServer is to start the Netty server
```java
@EnableMyNettyServer
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```
> the annotation of EnableMyNettyClient is to start the Netty client
```java
@EnableMyNettyClient
@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
```
***NettyController***
> Used to handle various requests from clients
```java
@Component
@NettyController
public class MyNettyController {

    @Autowired
    MyNettyTemplate myNettyTemplate;

    @NettyMapping("registry")
    public void registry(ChannelHandlerContext ctx, String seq) {
        myNettyTemplate.registry(ctx,seq);
    }

}

***Message Format***
@link{com.juno.framework.netty.beans.NettyMessage}
```java
public class NettyMessage {

    private String no;
    private String path;
    private Map<String,String> params;
    
    // getter and setter

}
```
* Visit http: // localhost: port / netty-api to learn about the interface and parameter information provided by netty server

```json
{
    "apis":[
        {
            "path":"/registry",
            "param":{
                "seq":"java.lang.String"
            }
        },
        {
            "path":"/ack-message",
            "param":{
                "message":"com.juno.framework.netty.beans.NettyMessage"
            }
        }
    ]
}
```

### Instalación 🔧
Download source code from github
```git bash
# git clone https://github.com/junochan/framework.git
```
The project coordinates are as follows
```xml
<groupId>com.juno.framework</groupId>
<artifactId>my-netty</artifactId>
<version>0.0.1-SNAPSHOT</version>
```
Use maven to deploy the project locally
```git bash
# cd MyNetty
# mvn install
```

## Versionado 📌
Usamos [Maven](https://maven.apache.org/) para el versionado. Para todas las versiones disponibles, mira los [tags en este repositorio](https://github.com/junochan/framework/tags).

## Licencia 📄

Este proyecto está bajo la Licencia (Tu Licencia) - mira el archivo [LICENSE](LICENSE) para detalles

## Expresiones de Gratitud 🎁

* Comenta a otros sobre este proyecto 📢
* Invita una cerveza 🍺 o un café ☕ a alguien del equipo. 
* Da las gracias públicamente.
* etc.