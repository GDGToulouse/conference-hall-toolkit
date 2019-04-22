# Tools to work with conference-hall CFP

You can activate and find the API_KEY here: `https://conference-hall.io/organizer/event/<EVENT_ID>/edit/integrations` 

## Display few stats 

```bash
java -jar build/libs/conference-hall-check.jar stats --event <EVENT_ID>  --api-key <API_KEY>
```

## Check new Talks with data issues 

```bash
java -jar build/libs/conference-hall-check.jar check --event <EVENT_ID>  --api-key <API_KEY>
```
