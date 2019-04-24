# Tools to work with conference-hall CFP

You can activate and find the API_KEY here: `https://conference-hall.io/organizer/event/<EVENT_ID>/edit/integrations` 

## Build

Clone this repo and execute `./gradlew fatJar`

_If needed, you may use `./gradlew clean` to clean the build directories._

## Display few stats 

```bash
java -jar build/libs/conference-hall-toolkit-all.jar stats --event <EVENT_ID>  --api-key <API_KEY>
```

## Check new Talks with data issues 

```bash
java -jar build/libs/conference-hall-toolkit-all.jar check --event <EVENT_ID>  --api-key <API_KEY>
```

Note: Already known talks with issue can be define into the local `KNOWN_TALKS_WITH_DATA_ISSUE.txt` file

## Generate selected talks

```bash
java -jar build/libs/conference-hall-toolkit-all.jar gen --event <EVENT_ID>  --api-key <API_KEY>
```

Notes: Selected talks should be define into the local `selected.txt` file
       Content are generated into the local `content` folder 
