require: functions.js
require: slotfilling/slotFilling.sc
  module = sys.zb-common
theme: /

    state: Start
        q!: $regex</start>
        a: Здраствуйте, моя задача помочь вам с выбором вакансии. 

    state: Hello
        intent!: /привет
        a: Привет привет

    state: Bye
        intent!: /пока
        a: До свидания! Надеюсь смог вам помочь.

    state: Clarification
        event!: noJobFound
        a: К сожалению, я не нашел вакансий по вашему запросу. Попробуйте изменить запрос или указать другие параметры.
    
    state: HandleApiResponse
        intent!: /поиск
        script:
            # Выполняем запрос через API для поиска вакансий
            fetchVacancies().then(function (res) {
                if (res.status === '200' && res.results.vacancies.length > 0) {
                    $session.vacancies = res.results.vacancies;
                    $reactions.answer("Вакансии успешно найдены! Перехожу к отображению...");
                    $session.nextState = 'ShowVacancies'
                } else {
                    # Если вакансий нет
                    $reactions.answer("К сожалению, вакансии по вашему запросу не найдены.");
                    $session.nextState = 'Clarification'
                }
            }).catch(function (err) {
                $reactions.answer("Что-то сервер барахлит. Не могу узнать погоду.");
            });

    state: ShowVacancies
        intent!: /показать_вакансии
        script:
            printVacancies($session.vacancies);

    state: NoMatch
        event!: noMatch
        a: Я не понял. Вы сказали: {{$request.query}}
        
    state: Profanity
        q!: * @mystem.obsc *
        a: Пожалуйста, избегайте нецензурных выражений. Мы стараемся поддерживать уважительную атмосферу.

    state: ProfanityAlt
        q!: * @mlps-obscene.obscene *
        a: Ваше сообщение содержит нецензурные слова. Пожалуйста, используйте более корректные выражения.