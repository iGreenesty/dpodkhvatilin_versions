def stateEvt = utils.lastState(subject);//запись в истории о последней смены статуса
def NEW_STATE = stateEvt.stateCode;//код предыдущего состояния

try {
  utils.edit(subject, [state : NEW_STATE]);
} catch (Exception e) {
  logger.error("[Возвращает проблему при наступлении времени атрибута] Ошибка при редактировании", e)
  throw e
}
