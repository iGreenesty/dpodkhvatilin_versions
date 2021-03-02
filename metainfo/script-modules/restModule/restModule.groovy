def getMessage(String type) {
    def modes = ['rate', 'vote', 'call_solving1'];
    String message = '';
    type = null == type ? null : type.toLowerCase();
    if((type) && modes.contains(type)) {
        switch(type) {
            case 'rate':
                message = 'ОТДЕЛ ДЛЯ ТЕСТИРОВАНИЯ API создан!';
                break;
            case 'vote':
                message = 'Ваша оценка выставлена!';
                break;
            case 'call_solving1':
                message = 'Заявка успешно переведена в статус решена.';
                break;
        }
    }
    return message;
}

def getCustomHtml(String str) {
  def params = str.tokenize(',')
  if(params.size() >= 3) {
    def action = params[0]
    def objectUUID = params[1] + '$' + params[2]
    def link = api.web.open(objectUUID)
    def root = utils.get('root', [:])
    def marks = [
      '1' : 'textMarkOne',
      '2' : 'textMarkTwo',
      '3' : 'textMarkThree',
      '4' : 'textMarkFour',
      '5' : 'textMarkFive'
    ]
  	def text = ''
  	switch(action) {
      case 'closed':
        text = '<span style="font-weight:600; line-height:40px;">Заявка закрыта</span>'
        break

      case 'resumed':
        text = '<span style="font-weight:600; line-height:40px;">Заявка возобновлена</span><span style="font-weight:600; line-height:40px;">'
        break

      default:
        if(marks.keySet().contains(action)) {
          def markText = root[marks[action]]
          markText = markText ? "<br/>" + markText : ''
          text = '<span style="font-weight:600; line-height:40px;">Спасибо за оценку!</span>' + markText
        }
    }

    String html = """<!doctype html>
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:v="urn:schemas-microsoft-com:vml" xmlns:o="urn:schemas-microsoft-com:office:office">

  <head>
    <title>${root?.title}</title>
    <!--[if !mso]><!-- -->
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <!--<![endif]-->
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <style type="text/css">
      #outlook a {
        padding: 0;
      }
      
      .ReadMsgBody {
        width: 100%;
      }
      
      .ExternalClass {
        width: 100%;
      }
      
      .ExternalClass * {
        line-height: 100%;
      }
      
      body {
        margin: 0;
        padding: 0;
        -webkit-text-size-adjust: 100%;
        -ms-text-size-adjust: 100%;
      }
      
      table,
      td {
        border-collapse: collapse;
        mso-table-lspace: 0pt;
        mso-table-rspace: 0pt;
      }
      
      img {
        border: 0;
        height: auto;
        line-height: 100%;
        outline: none;
        text-decoration: none;
        -ms-interpolation-mode: bicubic;
      }
      
      p {
        display: block;
        margin: 13px 0;
      }

    </style>
    <!--[if !mso]><!-->
    <style type="text/css">
      @media only screen and (max-width:480px) {
        @-ms-viewport {
          width: 320px;
        }
        @viewport {
          width: 320px;
        }
      }

    </style>
    <!--<![endif]-->
    <!--[if mso]>
<xml>
  <o:OfficeDocumentSettings>
    <o:AllowPNG/>
    <o:PixelsPerInch>96</o:PixelsPerInch>
  </o:OfficeDocumentSettings>
</xml>
<![endif]-->
    <!--[if lte mso 11]>
<style type="text/css">
  .outlook-group-fix {
    width:100% !important;
  }
</style>
<![endif]-->

    <!--[if !mso]><!-->
    <link href="https://fonts.googleapis.com/css?family=Ubuntu:300,400,500,700" rel="stylesheet" type="text/css">
    <style type="text/css">
      @import url(https://fonts.googleapis.com/css?family=Ubuntu:300,400,500,700);

    </style>
    <!--<![endif]-->
    <style type="text/css">
      @media only screen and (min-width:480px) {
        .mj-column-per-100 {
          width: 100%!important;
        }
      }

    </style>
  </head>

  <body style="background: #ffffff">

    <div class="mj-container">
      <!--[if mso | IE]>
      <table role="presentation" border="0" cellpadding="0" cellspacing="0" width="600" align="center" style="width:600px;">
        <tr>
          <td style="line-height:0px;font-size:0px;mso-line-height-rule:exactly;">
      <![endif]-->
      <div style="margin:30px auto 0 auto;max-width:600px;">
        <table role="presentation" cellpadding="0" cellspacing="0" style="font-size:0px;width:100%;" align="center" border="0">
          <tbody>
            <tr>
              <td style="text-align:center;vertical-align:top;direction:ltr;font-size:0px;padding:20px;">
                <!--[if mso | IE]>
      <table role="presentation" border="0" cellpadding="0" cellspacing="0">
        <tr>
          <td style="vertical-align:top;width:600px;">
      <![endif]-->
                <div class="mj-column-per-100 outlook-group-fix" style="vertical-align:top;display:inline-block;direction:ltr;font-size:13px;text-align:left;width:100%;">
                  <table role="presentation" cellpadding="0" cellspacing="0" width="100%" border="0">
                    <tbody>
                      <tr>
                        <td style="word-wrap:break-word;font-size:0px;padding:0px;" align="center">
                          <div style="cursor:auto;color:#000000;font-family:sans-serif;font-size:20px;line-height:30px;text-align:center;font-weight:500">
                          ${text}
                          </div>
                        </td>
                      </tr>
                      <tr>
                        <td style="word-wrap:break-word;font-size:0px;padding:20px 0px 10px 0px;" align="center">
                          <table role="presentation" cellpadding="0" cellspacing="0" style="border-collapse:separate;" align="center" border="0">
                            <tbody>
                              <tr>
                                <td><a href="${link}" style="display:inline-block; border:2px solid #1f54a2; border-radius:50px; color:#1f54a2; padding:13px 30px 13px 30px; font-family:Ubuntu, Helvetica, Arial, sans-serif; font-size:16px; font-weight:700; line-height:16px; text-transform:none; margin: 0; text-decoration: none" align="center" valign="middle">К заявке</a></td>
                              </tr>
                            </tbody>
                          </table>
                        </td>
                      </tr>
                    </tbody>
                  </table>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
    </div>
  </body>
</html>""";
   return html;
  }
}