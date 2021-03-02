//SCRIPTSD4000411
if (subject.parent)
{
	def totalAgreements = subject.recipientAgreements + subject.parent.recipientAgreements
	utils.edit(subject, ['recipientAgreements': totalAgreements])
}