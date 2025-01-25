package model

type Response struct {
	Id   int64  `json:"id"`
	Code int    `json:"code"`
	Data any    `json:"data"`
	Msg  string `json:"msg"`
}
