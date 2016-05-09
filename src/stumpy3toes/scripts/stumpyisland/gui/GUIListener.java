package stumpy3toes.scripts.stumpyisland.gui;

import stumpy3toes.scripts.stumpyisland.AccountType;
import stumpy3toes.scripts.stumpyisland.tasks.charactercreation.Gender;

public interface GUIListener {
    void closed();
    void started(AccountType accountType, Gender gender, boolean randomiseCharacter, boolean enterMainland,
                 boolean userEnterPin);
}
