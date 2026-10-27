RADIOBALKAN24 – ANDROID PROJEKT ZA BESPLATNU IZGRADNJU APK-a

VAŽNO
- Ovaj ZIP sadrži izvorni Android projekt i GitHub Actions workflow.
- APK još nije izgrađen niti testiran na stvarnom uređaju.
- Datoteka reference_latest_html_do_not_edit.html samo je referenca; objavljena Netlify stranica nije mijenjana.
- Aplikacija učitava postojeću stranicu: https://lighthearted-stardust-37d35c.netlify.app/
- Pozadinska reprodukcija i Bluetooth kontrole trebaju se testirati nakon izgradnje.

KAKO POKUŠATI IZGRADITI APK SAMO S MOBITELA
1. U pregledniku otvori https://github.com/ i prijavi se ili izradi besplatan račun.
2. Napravi NOVI PUBLIC repository, npr. RadioBalkan24 Novi-Android. Ne stavljaj lozinke, tokene ili tajne podatke u projekt.
3. Učitaj sadržaj ovog ZIP-a u repozitorij tako da mapa .github/workflows/build-apk.yml bude na tom putu u korijenu repozitorija. GitHub web sučelje može otežati prijenos cijele strukture mapa s mobitela; ako ne uspije, nemoj mijenjati nasumične postavke.
4. Otvori karticu Actions i odaberi “Build RadioBalkan24 Novi APK”.
5. Pritisni “Run workflow” i potvrdi pokretanje.
6. Ako postupak uspije, otvori završeni run, skrolaj do Artifacts i preuzmi “RadioBalkan24 Novi-debug-APK”.
7. Raspakiraj ZIP koji sadrži APK, zatim instaliraj APK. Android može tražiti dopuštenje za instalaciju iz tog preglednika/datoteke.

AKO IZGRADNJA NE USPIJE
Otvori run u Actions i sačuvaj ili pošalji poruku greške. Nemoj slati lozinke, tokene ili sigurnosne kodove.

NAPOMENA O PRIVATNOSTI
GitHub Actions se pokreće na javnom repozitoriju, pa projektni kod postaje javno vidljiv. Ne dodavaj privatne ključeve, osobne podatke ni tajne vrijednosti.
