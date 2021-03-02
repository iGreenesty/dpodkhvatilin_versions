//SCRIPTSD4000412
def message = "Нельзя удалить связь с системным соглашением. Системное соглашение используется для создания заявок по почте."
oldSubject.recipientAgreements.each {
  agreement ->	
  if (agreement.protected && !subject.recipientAgreements.contains(agreement)) {
    utils.throwReadableException(message, [] as String[], message, [] as String[])
  }
}