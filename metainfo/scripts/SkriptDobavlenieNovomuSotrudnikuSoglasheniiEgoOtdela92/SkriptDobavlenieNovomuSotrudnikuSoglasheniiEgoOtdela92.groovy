//SCRIPTSD4000417
def totalAgreements = subject.recipientAgreements + subject.parent.recipientAgreements
utils.edit(subject, ['recipientAgreements': totalAgreements])