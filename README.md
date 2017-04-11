# Homework Calculator - Bok Da Hoon
(), {} 를 허용하는 4측 연산 계산기를 스프링 부트 어플리케이션으로 작성하세요.

## Swagger URL
http://localhost:8080/swagger-ui.html

## Parameters
Name | Required | Description | Example
--------- | --------- | --------- | ---------
expression | required | 계산 표현식을 입력합니다. | 1 + 1
variable | optional | expression에 변수가 있는 경우 변수명과 같은 이름으로 query parameter를 보냅니다. | 3

## Example Response
```
{
  "result": 123
}
```

## HTTP Status Code
Code | Description
--------- | ---------
403 | Expression 형식 오류
404 | 변수명이 expression에 존재하는데 Query parameter에 해당 변수가 존재하지 않는 경우
503 | 변수의 값이 적절한 숫자 형식의 값이 아닌 경우

