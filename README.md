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

## Generate selected talks and speakers 

```bash
java -jar build/libs/conference-hall-toolkit-all.jar gen --event <EVENT_ID>  --api-key <API_KEY>
```

Notes: Selected talks should be define into the local `selected.txt` file
       Content are generated into the local `content` folder 

## Generate sponsors 

1. Update the `Formulaire Sponsors.csv` by downloading it at <https://docs.google.com/forms/d/1OzB5Y8f8mHt4WP2EH28hD3iFABVebrdb44BHvhfjm3o/edit#responses>
2. Update the `manual-sponsor.json` file to set `category`, `logoExtension`, and `jobs`

3. Build content file 

```bash
java -jar build/libs/conference-hall-toolkit-all.jar sponsor
```

Content are generated into the local `content/partners` folder

4. Copy missing sponors file to site
5. In the site project, UPLOAD into `static/images/partners/` folder the log USING THE NAME define into the content file
6. Complain to sponsors who have misfilled the form
7. Follow site instruction to test and push data
