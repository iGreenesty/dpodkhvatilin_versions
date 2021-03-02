def AUTOCREATION = 'autoCreation'   // автоматическое распространение по отделам
def AUTOCREATION_TEAM = 'autoCreationTe' // автоматическое распространение по командам
def PICK_ALL = 'allRecipients'
return (subject[AUTOCREATION] || subject[AUTOCREATION_TEAM] || subject[PICK_ALL]) ? "" : "Заданные условия не выполнились"