def NEW_STATE = 'inprogress'
try {
  utils.edit(subject, ['state' : NEW_STATE, '@user' : null])
} catch (Exception e) {
  logger.error('Смена статуса на "В работе". Произошла ошибка при возврате заявки в работу', e)
  def msg = "Возникла ошибка при возврате заявки в работу. Обратитесь к администратору"
  utils.throwReadableException(msg, [] as String[], msg, [] as String[])
}